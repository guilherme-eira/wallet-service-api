package io.github.guilherme_eira.wallet_service.application.port.out;

import io.github.guilherme_eira.wallet_service.domain.model.PasswordResetToken;
import io.github.guilherme_eira.wallet_service.domain.model.User;

import java.util.Optional;

public interface PasswordResetTokenRepository {
    void save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    void delete(PasswordResetToken token);
    void flush();
}
