package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository;

import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.UserEntity;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenJpaRepository extends JpaRepository<VerificationTokenEntity, Long> {
    Optional<VerificationTokenEntity> findByToken(String token);
    Optional<VerificationTokenEntity> findByUser(UserEntity user);
}
