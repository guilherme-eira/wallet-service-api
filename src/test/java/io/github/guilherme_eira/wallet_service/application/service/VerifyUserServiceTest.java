package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.RegisterOutput;
import io.github.guilherme_eira.wallet_service.application.exception.ExpiredVerificationTokenException;
import io.github.guilherme_eira.wallet_service.application.exception.VerificationTokenNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.AuthOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.VerificationTokenRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.VerificationToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyUserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    VerificationTokenRepository tokenRepository;

    @Mock
    AuthOutputMapper authMapper;

    @InjectMocks
    VerifyUserService service;

    @Test
    void shouldVerifyUserWhenTokenIsValid() {
        String tokenStr = "valid-token-uuid";
        User user = new User();
        user.setVerified(false);

        VerificationToken token = mock(VerificationToken.class);
        given(token.isExpired()).willReturn(false);
        given(token.getUser()).willReturn(user);

        given(tokenRepository.findByToken(tokenStr)).willReturn(Optional.of(token));
        given(userRepository.save(user)).willReturn(user);
        given(authMapper.toRegisterOutput(user, false))
                .willReturn(new RegisterOutput(UUID.randomUUID(), null, null, null, null));

        RegisterOutput result = service.execute(tokenStr);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        Assertions.assertTrue(savedUser.isVerified());
        Assertions.assertNotNull(savedUser.getUpdatedAt());

        verify(tokenRepository).delete(token);

        Assertions.assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionWhenTokenNotFound() {
        String tokenStr = "invalid-token";

        given(tokenRepository.findByToken(tokenStr)).willReturn(Optional.empty());

        Assertions.assertThrows(VerificationTokenNotFoundException.class, () -> {
            service.execute(tokenStr);
        });

        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsExpired() {
        String tokenStr = "expired-token";
        VerificationToken token = mock(VerificationToken.class);

        given(tokenRepository.findByToken(tokenStr)).willReturn(Optional.of(token));
        given(token.isExpired()).willReturn(true);

        Assertions.assertThrows(ExpiredVerificationTokenException.class, () -> {
            service.execute(tokenStr);
        });

        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }
}