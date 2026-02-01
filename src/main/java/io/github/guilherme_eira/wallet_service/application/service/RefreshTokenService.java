package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.in.RefreshTokenUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final TokenProvider tokenProvider;
    private final UserRepository repository;

    @Override
    public AuthOutput execute(String token) {
        var email = tokenProvider.getSubject(token);
        var user = repository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        var accessToken = tokenProvider.generateAccessToken(user, "ROLE_FULL_ACCESS");
        var refreshToken = tokenProvider.generateRefreshToken(user);
        return new AuthOutput(accessToken, refreshToken, false);
    }
}
