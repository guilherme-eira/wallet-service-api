package io.github.guilherme_eira.wallet_service.application.port.out;

import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    void save(VerificationToken token);
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
    void delete(VerificationToken token);
    void flush();
}
