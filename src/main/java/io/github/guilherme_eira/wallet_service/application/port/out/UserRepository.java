package io.github.guilherme_eira.wallet_service.application.port.out;

import io.github.guilherme_eira.wallet_service.domain.model.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByTaxId(String taxId);
    Boolean existsByEmailOrTaxId(String email, String taxId);
    void deleteByVerifiedFalseAndCreatedAtBefore(LocalDateTime cutOffDate);
}
