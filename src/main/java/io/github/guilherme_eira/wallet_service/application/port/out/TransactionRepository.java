package io.github.guilherme_eira.wallet_service.application.port.out;

import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    void save(Transaction transaction);
    BigDecimal sumDailyUsage(UUID walletId, LocalDateTime startDate);
    Optional<Transaction> findById(UUID id);
    Page<Transaction> findAllByWalletId(UUID id, Pageable pageable);
}
