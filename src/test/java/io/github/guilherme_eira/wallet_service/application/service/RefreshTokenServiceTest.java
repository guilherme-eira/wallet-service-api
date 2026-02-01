package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    TokenProvider tokenProvider;

    @Mock
    UserRepository repository;

    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Test
    void shouldRotateTokensSuccessfullyWhenTokenIsValidAndUserExists() {

        String oldRefreshToken = "old_refresh_token_jwt";
        String email = "john@test.com";
        String newAccessToken = "new_access_token_jwt";
        String newRefreshToken = "new_refresh_token_jwt";

        User user = new User();
        user.setEmail(email);

        BDDMockito.given(tokenProvider.getSubject(oldRefreshToken))
                .willReturn(email);

        BDDMockito.given(repository.findByEmail(email))
                .willReturn(Optional.of(user));

        BDDMockito.given(tokenProvider.generateAccessToken(user, "ROLE_FULL_ACCESS"))
                .willReturn(newAccessToken);

        BDDMockito.given(tokenProvider.generateRefreshToken(user))
                .willReturn(newRefreshToken);

        AuthOutput output = refreshTokenService.execute(oldRefreshToken);

        Assertions.assertEquals(newAccessToken, output.token());
        Assertions.assertEquals(newRefreshToken, output.refreshToken());
        Assertions.assertFalse(output.requiresMfa());

        verify(tokenProvider).getSubject(oldRefreshToken);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenTokenIsValidButUserWasDeleted() {

        String oldRefreshToken = "orphan_token";
        String email = "ghost@test.com";

        BDDMockito.given(tokenProvider.getSubject(oldRefreshToken))
                .willReturn(email);

        BDDMockito.given(repository.findByEmail(email))
                .willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            refreshTokenService.execute(oldRefreshToken);
        });

        verify(tokenProvider, never()).generateAccessToken(any(), any());
        verify(tokenProvider, never()).generateRefreshToken(any());
    }
}