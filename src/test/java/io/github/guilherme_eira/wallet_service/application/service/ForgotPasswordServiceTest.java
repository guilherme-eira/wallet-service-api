package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.port.out.NotificationGateway;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordResetTokenRepository tokenRepository;

    @Mock
    NotificationGateway notificationGateway;

    @InjectMocks
    ForgotPasswordService service;

    @Test
    void shouldSendEmailWhenUserIsValidAndHasNoPreviousToken() {

        String email = "john@test.com";
        User user = new User();
        user.setEmail(email);
        user.setVerified(true);
        user.setActive(true);

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        BDDMockito.given(tokenRepository.findByUser(user)).willReturn(Optional.empty());

        service.execute(email);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        Assertions.assertEquals(user, savedToken.getUser());
        Assertions.assertNotNull(savedToken.getToken());

        verify(notificationGateway).sendResetPasswordResetEmail(email, savedToken.getToken());
    }

    @Test
    void shouldReplaceTokenWhenPreviousTokenExistsAndIsNotBlocked() {

        String email = "john@test.com";
        User user = new User();
        user.setVerified(true);
        user.setActive(true);

        PasswordResetToken oldToken = new PasswordResetToken();
        oldToken.setCreatedAt(LocalDateTime.now().minusHours(1));

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        BDDMockito.given(tokenRepository.findByUser(user)).willReturn(Optional.of(oldToken));

        service.execute(email);

        verify(tokenRepository).delete(oldToken);
        verify(tokenRepository).flush();
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(notificationGateway).sendResetPasswordResetEmail(eq(email), any());
    }

    @Test
    void shouldDoNothingWhenRateLimitIsExceeded() {

        String email = "spammer@test.com";
        User user = new User();
        user.setVerified(true);
        user.setActive(true);

        PasswordResetToken recentToken = new PasswordResetToken();
        recentToken.setCreatedAt(LocalDateTime.now().minusMinutes(1));

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        BDDMockito.given(tokenRepository.findByUser(user)).willReturn(Optional.of(recentToken));

        service.execute(email);

        verify(tokenRepository, never()).delete(any());
        verify(tokenRepository, never()).save(any());
        verify(notificationGateway, never()).sendResetPasswordResetEmail(any(), any());
    }

    @Test
    void shouldReturnSilentlyWhenUserDoesNotExist() {

        String email = "ghost@test.com";
        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        service.execute(email);

        verify(notificationGateway, never()).sendResetPasswordResetEmail(any(), any());
    }

    @Test
    void shouldReturnSilentlyWhenUserIsNotVerified() {
        String email = "unverified@test.com";
        User user = new User();
        user.setVerified(false);

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        service.execute(email);

        verify(tokenRepository, never()).save(any());
        verify(notificationGateway, never()).sendResetPasswordResetEmail(any(), any());
    }

    @Test
    void shouldCatchExceptionsAndLogWithoutThrowing() {

        String email = "error@test.com";
        BDDMockito.given(userRepository.findByEmail(any()))
                .willThrow(new RuntimeException("Database Down"));

        Assertions.assertDoesNotThrow(() -> service.execute(email));
    }
}