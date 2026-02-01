package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository;

import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailAndActiveTrue(String email);
    Optional<UserEntity> findByTaxIdAndActiveTrue(String email);
    void deleteByVerifiedFalseAndCreatedAtBefore(LocalDateTime cutoffDate);
    Boolean existsByActiveTrueAndEmailOrActiveTrueAndTaxId(String email, String taxId);
}
