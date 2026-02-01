package io.github.guilherme_eira.wallet_service.application.port.out;

import io.github.guilherme_eira.wallet_service.domain.model.User;

public interface TokenProvider {
    String generateAccessToken(User user, String role);
    String generateRefreshToken(User user);
    String getSubject(String token);
    String getRole(String token);
}
