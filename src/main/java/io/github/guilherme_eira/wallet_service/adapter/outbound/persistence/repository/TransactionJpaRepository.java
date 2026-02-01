package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository;

import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
    @Query("""
                SELECT COALESCE(SUM(t.amount), 0) 
                FROM TransactionEntity t 
                WHERE t.createdAt >= :startOfDay
                  AND (
                      (t.sender.id = :walletId AND t.type = 'WITHDRAW') 
                      OR 
                      (t.sender.id = :walletId AND t.type = 'TRANSFER')
                  )
            """)
    BigDecimal sumDailyUsage(UUID walletId, LocalDateTime startOfDay);

    @Query("""
                SELECT t 
                FROM TransactionEntity t 
                WHERE t.sender.id = :walletId 
                OR t.receiver.id = :walletId
            """)
    Page<TransactionEntity> findAllByWalletId(UUID walletId, Pageable pageable);
}
