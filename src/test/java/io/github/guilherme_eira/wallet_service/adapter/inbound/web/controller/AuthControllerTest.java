package io.github.guilherme_eira.wallet_service.adapter.inbound.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.LoginRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.ValidateMfaRequest;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.AuthMapper;
import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;
import io.github.guilherme_eira.wallet_service.application.port.in.*;
import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.infra.security.SecurityConfig;
import io.github.guilherme_eira.wallet_service.infra.security.SecurityFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean private LoginUseCase loginUseCase;
    @MockitoBean private RefreshTokenUseCase refreshTokenUseCase;
    @MockitoBean private ValidateMfaUseCase validateMfaUseCase;
    @MockitoBean private RegisterUseCase registerUseCase;
    @MockitoBean private VerifyUserUseCase verifyUserUseCase;
    @MockitoBean private ResendVerificationUseCase resendVerificationUseCase;
    @MockitoBean private ForgotPasswordUseCase forgotPasswordUseCase;
    @MockitoBean private ResetPasswordUseCase resetPasswordUseCase;
    @MockitoBean private AuthMapper authMapper;

    @Test
    void shouldAllowPublicAccessToLogin() throws Exception {

        LoginRequest req = new LoginRequest("test@email.com", "password123");
        AuthOutput output = new AuthOutput("jwt_token", "refresh_token", false);

        BDDMockito.given(loginUseCase.execute(any())).willReturn(output);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenPayloadIsInvalid() throws Exception {
        LoginRequest req = new LoginRequest("not-an-email", "");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDenyAccessToProtectedResourceWithoutToken() throws Exception {
        ValidateMfaRequest req = new ValidateMfaRequest("123456");

        mockMvc.perform(post("/auth/validate-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAccessWithValidTokenAndRole() throws Exception {
        String token = "valid_token";
        String email = "user@test.com";

        BDDMockito.given(tokenProvider.getSubject(token)).willReturn(email);
        BDDMockito.given(tokenProvider.getRole(token)).willReturn("ROLE_PRE_AUTH");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setActive(true);
        user.setVerified(true);
        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        AuthOutput output = new AuthOutput("new_token", "new_refresh", false);
        BDDMockito.given(validateMfaUseCase.execute(any())).willReturn(output);

        ValidateMfaRequest req = new ValidateMfaRequest("123456");

        mockMvc.perform(post("/auth/validate-code")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve negar acesso (403) se o Token tiver a Role errada")
    void shouldDenyAccessWhenRoleIsIncorrect() throws Exception {
        String token = "valid_token_wrong_role";
        String email = "user@test.com";

        BDDMockito.given(tokenProvider.getSubject(token)).willReturn(email);
        BDDMockito.given(tokenProvider.getRole(token)).willReturn("ROLE_FULL_ACCESS");

        User user = new User();
        user.setActive(true);
        user.setVerified(true);
        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        ValidateMfaRequest req = new ValidateMfaRequest("123456");

        mockMvc.perform(post("/auth/validate-code")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve negar acesso (403) se o Usuário estiver INATIVO")
    void shouldDenyAccessWhenUserIsInactive() throws Exception {
        String token = "token_inactive_user";
        String email = "inactive@test.com";

        BDDMockito.given(tokenProvider.getSubject(token)).willReturn(email);

        User inactiveUser = new User();
        inactiveUser.setActive(false);
        inactiveUser.setVerified(true);

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(inactiveUser));

        ValidateMfaRequest req = new ValidateMfaRequest("123456");

        mockMvc.perform(post("/auth/validate-code")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve negar acesso (403) se o Usuário NÃO for VERIFICADO")
    void shouldDenyAccessWhenUserIsNotVerified() throws Exception {
        String token = "token_unverified";
        String email = "unverified@test.com";

        BDDMockito.given(tokenProvider.getSubject(token)).willReturn(email);

        User unverifiedUser = new User();
        unverifiedUser.setActive(true);
        unverifiedUser.setVerified(false);

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(unverifiedUser));

        ValidateMfaRequest req = new ValidateMfaRequest("123456");

        mockMvc.perform(post("/auth/validate-code")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}