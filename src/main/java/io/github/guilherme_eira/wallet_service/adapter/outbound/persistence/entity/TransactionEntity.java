package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity;

import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionEntity {
    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id")
    private WalletEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private WalletEntity receiver;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
