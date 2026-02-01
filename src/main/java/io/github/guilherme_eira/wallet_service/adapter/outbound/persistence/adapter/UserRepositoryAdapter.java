package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.adapter;

import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.UserMapper;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository.UserJpaRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmailAndActiveTrue(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByTaxId(String taxId) {
        return repository.findByTaxIdAndActiveTrue(taxId).map(mapper::toDomain);
    }

    @Override
    public Boolean existsByEmailOrTaxId(String email, String taxId) {
        return repository.existsByActiveTrueAndEmailOrActiveTrueAndTaxId(email, taxId);
    }

    @Override
    public void deleteByVerifiedFalseAndCreatedAtBefore(LocalDateTime cutOffDate) {
        repository.deleteByVerifiedFalseAndCreatedAtBefore(cutOffDate);
    }
}
