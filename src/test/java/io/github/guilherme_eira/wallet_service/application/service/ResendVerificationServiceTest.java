package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.port.out.NotificationGateway;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.VerificationTokenRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.VerificationToken;
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
class ResendVerificationServiceTest {

    @Mock UserRepository userRepository;
    @Mock VerificationTokenRepository verificationTokenRepository;
    @Mock NotificationGateway notificationGateway;

    @InjectMocks ResendVerificationService service;

    @Test
    void shouldResendEmailWhenUserIsValidAndHasNoBlockedToken() {
        String email = "john@test.com";
        User user = new User();
        user.setEmail(email);
        user.setActive(true);
        user.setVerified(false);

        VerificationToken oldToken = mock(VerificationToken.class);
        BDDMockito.given(oldToken.isResendBlocked()).willReturn(false);

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        BDDMockito.given(verificationTokenRepository.findByUser(user)).willReturn(Optional.of(oldToken));

        service.execute(email);

        verify(verificationTokenRepository).delete(oldToken);
        verify(verificationTokenRepository).flush();

        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository).save(captor.capture());

        VerificationToken newToken = captor.getValue();
        Assertions.assertNotNull(newToken.getToken());

        verify(notificationGateway).sendVerificationEmail(email, newToken.getToken());
    }

    @Test
    void shouldDoNothingWhenRateLimitIsExceeded() {
        String email = "spammer@test.com";
        User user = new User();
        user.setActive(true);
        user.setVerified(false);

        VerificationToken recentToken = mock(VerificationToken.class);
        BDDMockito.given(recentToken.isResendBlocked()).willReturn(true);

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        BDDMockito.given(verificationTokenRepository.findByUser(user)).willReturn(Optional.of(recentToken));

        service.execute(email);

        verify(verificationTokenRepository, never()).delete(any());
        verify(verificationTokenRepository, never()).save(any());
        verify(notificationGateway, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void shouldReturnSilentlyWhenUserAlreadyVerified() {
        String email = "verified@test.com";
        User user = new User();
        user.setVerified(true);

        BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        service.execute(email);

        verify(notificationGateway, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void shouldReturnSilentlyWhenUserNotFound() {

        BDDMockito.given(userRepository.findByEmail("ghost@test.com"))
                .willReturn(Optional.empty());

        service.execute("ghost@test.com");

        verify(notificationGateway, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void shouldSuppressExceptionsWhenErrorOccurs() {

        BDDMockito.given(userRepository.findByEmail(any()))
                .willThrow(new RuntimeException("Database error"));

        Assertions.assertDoesNotThrow(() -> service.execute("error@test.com"));
    }
}