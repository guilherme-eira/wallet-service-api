package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.ValidateMfaCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectMfaCodeException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.out.MfaProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ValidateMfaServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    MfaProvider mfaProvider;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    ValidateMfaService service;

    @Test
    void shouldReturnTokensWhenMfaCodeIsCorrect() {
        var userId = UUID.randomUUID();
        var code = "123456";
        var secret = "secret_key";
        var cmd = new ValidateMfaCommand(userId, code);

        var user = new User();
        user.setId(userId);
        user.setTwoFactorSecret(secret);

        given(repository.findById(userId)).willReturn(Optional.of(user));
        given(mfaProvider.validateMfaCode(code, secret)).willReturn(true);
        given(tokenProvider.generateAccessToken(user, "ROLE_FULL_ACCESS")).willReturn("access_token");
        given(tokenProvider.generateRefreshToken(user)).willReturn("refresh_token");

        AuthOutput output = service.execute(cmd);

        Assertions.assertEquals("access_token", output.token());
        Assertions.assertEquals("refresh_token", output.refreshToken());
        Assertions.assertFalse(output.requiresMfa());

        verify(tokenProvider).generateAccessToken(user, "ROLE_FULL_ACCESS");
    }

    @Test
    void shouldThrowExceptionWhenMfaCodeIsIncorrect() {
        var userId = UUID.randomUUID();
        var code = "wrong_code";
        var secret = "secret_key";
        var cmd = new ValidateMfaCommand(userId, code);

        var user = new User();
        user.setId(userId);
        user.setTwoFactorSecret(secret);

        given(repository.findById(userId)).willReturn(Optional.of(user));
        given(mfaProvider.validateMfaCode(code, secret)).willReturn(false);

        Assertions.assertThrows(IncorrectMfaCodeException.class, () -> service.execute(cmd));

        verify(tokenProvider, never()).generateAccessToken(any(), any());
        verify(tokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        var cmd = new ValidateMfaCommand(UUID.randomUUID(), "123456");

        given(repository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> service.execute(cmd));

        verify(mfaProvider, never()).validateMfaCode(any(), any());
        verify(tokenProvider, never()).generateAccessToken(any(), any());
    }
}