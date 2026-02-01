package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WalletEntity {

    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity owner;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String transactionPin;

    @Column(nullable = false)
    private Integer pinAttempts;

    private LocalDateTime pinBlockedUntil;

    @Column(nullable = false)
    private BigDecimal transactionLimit;

    @Column(nullable = false)
    private BigDecimal nightLimit;

    @Column(nullable = false)
    private BigDecimal dailyLimit;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}