package io.github.guilherme_eira.wallet_service.domain.model;

import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private UUID id;
    private Wallet sender;
    private Wallet receiver;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;

    public static Transaction create(Wallet sender, Wallet receiver, BigDecimal amount, TransactionType type){
        return new Transaction(
                UUID.randomUUID(),
                sender,
                receiver,
                amount,
                type,
                LocalDateTime.now()
        );
    }

    public Transaction(UUID id, Wallet sender, Wallet receiver, BigDecimal amount, TransactionType type, LocalDateTime createdAt) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Transaction() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Wallet getSender() {
        return sender;
    }

    public void setSender(Wallet sender) {
        this.sender = sender;
    }

    public Wallet getReceiver() {
        return receiver;
    }

    public void setReceiver(Wallet receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
