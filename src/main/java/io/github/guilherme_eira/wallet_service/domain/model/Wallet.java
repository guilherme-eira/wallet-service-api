package io.github.guilherme_eira.wallet_service.domain.model;

import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.github.guilherme_eira.wallet_service.domain.exception.InsufficientBalanceException;
import io.github.guilherme_eira.wallet_service.domain.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Wallet {
    private UUID id;
    private User owner;
    private BigDecimal balance;
    private String transactionPin;
    private Integer pinAttempts;
    private LocalDateTime pinBlockedUntil;
    private BigDecimal transactionLimit;
    private BigDecimal nightLimit;
    private BigDecimal dailyLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Wallet create(User owner, String transactionPin) {
        return new Wallet(
                UUID.randomUUID(),
                owner,
                BigDecimal.ZERO,
                transactionPin,
                0,
                null,
                owner.getType() == UserType.MERCHANT? new BigDecimal("5000.00") : new BigDecimal("2000.00"),
                new BigDecimal("1000.00"),
                owner.getType() == UserType.MERCHANT? new BigDecimal("15000.00") : new BigDecimal("5000.00"),
                LocalDateTime.now(),
                null
        );
    }
    public Wallet(UUID id, User owner, BigDecimal balance, String transactionPin, Integer pinAttempts, LocalDateTime pinBlockedUntil, BigDecimal transactionLimit, BigDecimal nightLimit, BigDecimal dailyLimit, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
        this.transactionPin = transactionPin;
        this.pinAttempts = pinAttempts;
        this.pinBlockedUntil = pinBlockedUntil;
        this.transactionLimit = transactionLimit;
        this.nightLimit = nightLimit;
        this.dailyLimit = dailyLimit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Wallet() {
    }


    public Boolean isPinBlocked() {
        return pinBlockedUntil != null && LocalDateTime.now().isBefore(pinBlockedUntil);
    }

    public void resetAttempts() {
        this.pinAttempts = 0;
    }

    public void incrementAttempts() {
        this.pinAttempts = this.pinAttempts + 1;
    }

    public void debit(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
        this.balance = this.balance.add(amount);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getTransactionPin() {
        return transactionPin;
    }

    public void setTransactionPin(String transactionPin) {
        this.transactionPin = transactionPin;
    }

    public Integer getPinAttempts() {
        return pinAttempts;
    }

    public void setPinAttempts(Integer pinAttempts) {
        this.pinAttempts = pinAttempts;
    }

    public LocalDateTime getPinBlockedUntil() {
        return pinBlockedUntil;
    }

    public void setPinBlockedUntil(LocalDateTime pinBlockedUntil) {
        this.pinBlockedUntil = pinBlockedUntil;
    }

    public BigDecimal getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(BigDecimal transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public BigDecimal getNightLimit() {
        return nightLimit;
    }

    public void setNightLimit(BigDecimal nightLimit) {
        this.nightLimit = nightLimit;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
