package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.adapter;

import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.UserMapper;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.VerificationTokenMapper;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository.VerificationTokenJpaRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.VerificationTokenRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationTokenRepositoryAdapter implements VerificationTokenRepository {

    private final VerificationTokenJpaRepository repository;
    private final UserMapper userMapper;
    private final VerificationTokenMapper verificationTokenMapper;

    @Override
    public void save(VerificationToken token) {
        repository.save(verificationTokenMapper.toEntity(token));
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return repository.findByToken(token).map(verificationTokenMapper::toDomain);
    }

    @Override
    public Optional<VerificationToken> findByUser(User user) {
        var userEntity = userMapper.toEntity(user);
        return repository.findByUser(userEntity).map(verificationTokenMapper::toDomain);
    }

    @Override
    public void delete(VerificationToken token) {
        repository.delete(verificationTokenMapper.toEntity(token));
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
