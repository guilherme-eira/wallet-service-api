package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository;

import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.UserEntity;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.WalletEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface WalletJpaRepository extends JpaRepository<WalletEntity, UUID> {
    Optional<WalletEntity> findByOwner(UserEntity owner);
    Optional<WalletEntity> findByOwnerId(UUID id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletEntity w WHERE w.owner = :owner")
    Optional<WalletEntity> findByOwnerWithLock(UserEntity owner);
}
