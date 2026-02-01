package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.adapter;

import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.UserMapper;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.WalletMapper;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository.WalletJpaRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletRepositoryAdapter implements WalletRepository {

    private final WalletJpaRepository repository;
    private final WalletMapper walletMapper;
    private final UserMapper userMapper;

    @Override
    public void save(Wallet wallet) {
        repository.save(walletMapper.toEntity(wallet));
    }

    @Override
    public Optional<Wallet> findByUserId(UUID id) {
        return repository.findByOwnerId(id).map(walletMapper::toDomain);
    }

    @Override
    public Optional<Wallet> findByUser(User user) {
        return repository.findByOwner(userMapper.toEntity(user)).map(walletMapper::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserWithLock(User user) {
        return repository.findByOwnerWithLock(userMapper.toEntity(user)).map(walletMapper::toDomain);
    }

}
