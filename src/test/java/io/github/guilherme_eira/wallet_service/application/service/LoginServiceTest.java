package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.LoginCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;
import io.github.guilherme_eira.wallet_service.application.exception.*;
import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenProvider tokenProvider;

    @InjectMocks LoginService loginService;

    @Test
    @DisplayName("Deve lançar erro genérico se usuário não existir (Evita Enumeração de Usuários)")
    void shouldThrowExceptionWhenUserNotFound() {
        var cmd = new LoginCommand("notfound@test.com", "123456");

        given(userRepository.findByEmail(cmd.email())).willReturn(Optional.empty());

        assertThrows(IncorrectCredentialsException.class, () -> loginService.execute(cmd));

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Deve bloquear imediatamente se a conta já estiver bloqueada temporariamente")
    void shouldFailFastWhenUserIsAlreadyBlocked() {
        var cmd = new LoginCommand("blocked@test.com", "anyPass");
        var user = new User();
        user.setLoginBlockedUntil(LocalDateTime.now().plusMinutes(10));

        given(userRepository.findByEmail(cmd.email())).willReturn(Optional.of(user));

        assertThrows(LoginBlockedException.class, () -> loginService.execute(cmd));

        verify(passwordEncoder, never()).matches(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro de não verificado antes de validar senha")
    void shouldThrowExceptionWhenUserNotVerified() {
        var cmd = new LoginCommand("unverified@test.com", "pass");
        var user = new User();
        user.setActive(true);
        user.setVerified(false);

        given(userRepository.findByEmail(cmd.email())).willReturn(Optional.of(user));

        assertThrows(UserNotVerifiedException.class, () -> loginService.execute(cmd));

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Deve incrementar tentativas e lançar erro quando a senha não bater")
    void shouldIncrementAttemptsWhenPasswordIsIncorrect() {
        var cmd = new LoginCommand("john@test.com", "wrongPass");
        var user = new User();
        user.setActive(true);
        user.setVerified(true);
        user.setPassword("hashedPass");
        user.setLoginAttempts(0);

        given(userRepository.findByEmail(cmd.email())).willReturn(Optional.of(user));

        given(passwordEncoder.matches(cmd.password(), user.getPassword())).willReturn(false);

        assertThrows(IncorrectCredentialsException.class, () -> loginService.execute(cmd));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(1, savedUser.getLoginAttempts());
        assertNull(savedUser.getLoginBlockedUntil());
    }

    @Test
    @DisplayName("Deve bloquear o usuário na 3ª tentativa falha")
    void shouldBlockUserWhenAttemptsReachLimit() {
        var cmd = new LoginCommand("john@test.com", "wrongPass");
        var user = new User();
        user.setActive(true);
        user.setVerified(true);
        user.setPassword("hashedPass");
        user.setLoginAttempts(2);

        given(userRepository.findByEmail(cmd.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(cmd.password(), user.getPassword())).willReturn(false);

        assertThrows(IncorrectCredentialsException.class, () -> loginService.execute(cmd));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(3, savedUser.getLoginAttempts());
        assertNotNull(savedUser.getLoginBlockedUntil(), "Deveria ter definido data de bloqueio");
    }

    @Test
    @DisplayName("Sucesso: Login padrão (Sem MFA) deve zerar tentativas e retornar Full Access")
    void shouldLoginSuccessfullyAndResetAttempts() {
        var cmd = new LoginCommand("john@test.com", "correctPass");
        var user = new User();
        user.setActive(true);
        user.setVerified(true);
        user.setPassword("hashedPass");
        user.setTwoFactorActive(false);
        user.setLoginAttempts(2);

        given(userRepository.findByEmail(cmd.email())).willReturn(Optional.of(user));

        given(passwordEncoder.matches(cmd.password(), user.getPassword())).willReturn(true);

        given(tokenProvider.generateAccessToken(user, "ROLE_FULL_ACCESS")).willReturn("access-jwt");
        given(tokenProvider.generateRefreshToken(user)).willReturn("refresh-jwt");

        AuthOutput output = loginService.execute(cmd);

        assertEquals("access-jwt", output.token());
        assertEquals("refresh-jwt", output.refreshToken());
        assertFalse(output.requiresMfa());

        assertEquals(0, user.getLoginAttempts(), "Deve zerar tentativas após sucesso");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Sucesso: Login com MFA ativo deve retornar token PRE_AUTH e exigir validação")
    void shouldGeneratePreAuthToken_WhenMfaIsEnabled() {
        var cmd = new LoginCommand("john@test.com", "correctPass");
        var user = new User();
        user.setActive(true);
        user.setVerified(true);
        user.setPassword("hashedPass");
        user.setLoginAttempts(0);
        user.setTwoFactorActive(true);

        given(userRepository.findByEmail(cmd.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(cmd.password(), user.getPassword())).willReturn(true);

        given(tokenProvider.generateAccessToken(user, "ROLE_PRE_AUTH")).willReturn("pre-auth-jwt");

        AuthOutput output = loginService.execute(cmd);

        assertEquals("pre-auth-jwt", output.token());
        assertNull(output.refreshToken(), "Não deve gerar refresh token ainda");
        assertTrue(output.requiresMfa());

        verify(tokenProvider, never()).generateRefreshToken(any());
        verify(userRepository, never()).save(any());
    }
}