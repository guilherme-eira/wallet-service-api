package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.adapter;

import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.TransactionMapper;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository.TransactionJpaRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.TransactionRepository;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionMapper mapper;
    private final TransactionJpaRepository repository;

    @Override
    public void save(Transaction transaction) {
        repository.save(mapper.toEntity(transaction));
    }

    @Override
    public BigDecimal sumDailyUsage(UUID walletId, LocalDateTime startDate) {
        return repository.sumDailyUsage(walletId, startDate);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Transaction> findAllByWalletId(UUID id, Pageable pageable) {
        return repository.findAllByWalletId(id, pageable).map(mapper::toDomain);
    }


}
