package io.github.guilherme_eira.wallet_service.application.port.out;

import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    void save(Wallet wallet);
    Optional<Wallet> findByUserId(UUID id);
    Optional<Wallet> findByUser(User user);
    Optional<Wallet> findByUserWithLock(User user);
}
