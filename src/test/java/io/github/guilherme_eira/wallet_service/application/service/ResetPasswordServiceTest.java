package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.ResetPasswordCommand;
import io.github.guilherme_eira.wallet_service.application.exception.ExpiredPasswordResetTokenException;
import io.github.guilherme_eira.wallet_service.application.exception.PasswordResetTokenNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordResetTokenRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.PasswordResetToken;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest {

    @Mock PasswordResetTokenRepository tokenRepository;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder encoder;

    @InjectMocks ResetPasswordService service;

    @Test
    void shouldResetPasswordSuccessfullyWhenTokenIsValid() {
        String tokenStr = "valid-token-123";
        String newPasswordRaw = "NewStrongPass123!";
        String encodedPassword = "encoded_new_pass";

        var command = new ResetPasswordCommand(tokenStr, newPasswordRaw);

        User user = new User();
        user.setPassword("old_pass");

        PasswordResetToken token = mock(PasswordResetToken.class);
        BDDMockito.given(token.isExpired()).willReturn(false);
        BDDMockito.given(token.getUser()).willReturn(user);

        BDDMockito.given(tokenRepository.findByToken(tokenStr))
                .willReturn(Optional.of(token));

        BDDMockito.given(encoder.encode(newPasswordRaw))
                .willReturn(encodedPassword);

        service.execute(command);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        Assertions.assertEquals(encodedPassword, savedUser.getPassword());
        Assertions.assertNotNull(savedUser.getUpdatedAt());

        verify(tokenRepository).delete(token);
    }

    @Test
    void shouldThrowExceptionWhenTokenNotFound() {
        var command = new ResetPasswordCommand("invalid-token", "Pass123!");

        BDDMockito.given(tokenRepository.findByToken(any()))
                .willReturn(Optional.empty());

        Assertions.assertThrows(PasswordResetTokenNotFoundException.class, () -> {
            service.execute(command);
        });

        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsExpired() {
        var command = new ResetPasswordCommand("expired-token", "Pass123!");

        PasswordResetToken token = mock(PasswordResetToken.class);
        BDDMockito.given(token.isExpired()).willReturn(true);

        BDDMockito.given(tokenRepository.findByToken(any()))
                .willReturn(Optional.of(token));

        Assertions.assertThrows(ExpiredPasswordResetTokenException.class, () -> {
            service.execute(command);
        });

        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }
}