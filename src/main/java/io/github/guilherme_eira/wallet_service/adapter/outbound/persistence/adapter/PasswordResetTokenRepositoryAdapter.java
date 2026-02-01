package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.adapter;

import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.PasswordResetTokenMapper;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.UserMapper;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository.PasswordResetTokenJpaRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordResetTokenRepository;
import io.github.guilherme_eira.wallet_service.domain.model.PasswordResetToken;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository repository;
    private final PasswordResetTokenMapper passwordResetTokenMapper;
    private final UserMapper userMapper;

    @Override
    public void save(PasswordResetToken token) {
        repository.save(passwordResetTokenMapper.toEntity(token));
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return repository.findByToken(token).map(passwordResetTokenMapper::toDomain);
    }

    @Override
    public Optional<PasswordResetToken> findByUser(User user) {
        return repository.findByUser(userMapper.toEntity(user)).map(passwordResetTokenMapper::toDomain);
    }

    @Override
    public void delete(PasswordResetToken token) {
        repository.delete(passwordResetTokenMapper.toEntity(token));
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
