package io.github.guilherme_eira.wallet_service.application.validator.impl;

import io.github.guilherme_eira.wallet_service.application.exception.LimitExceededException;
import io.github.guilherme_eira.wallet_service.application.port.out.TransactionRepository;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BalanceAndLimitValidatorTest {

    @Mock TransactionRepository transactionRepository;
    BalanceAndLimitValidator limitValidator;

    @Test
    void shouldThrowExceptionWhenNightLimitExceeded() {
        Clock nightClock = Clock.fixed(
                Instant.parse("2024-01-01T23:00:00Z"),
                ZoneId.of("UTC")
        );

        limitValidator = new BalanceAndLimitValidator(transactionRepository, nightClock);

        Wallet sender = new Wallet();
        sender.setNightLimit(new BigDecimal("1000.00"));

        BigDecimal amount = new BigDecimal("1000.01");

        Assertions.assertThrows(LimitExceededException.class, () ->
                limitValidator.validate(sender, amount, "1234")
        );
    }

    @Test
    void shouldPassWhenAmountIsAboveNightLimitButItIsDayTime() {
        Clock dayClock = Clock.fixed(
                Instant.parse("2024-01-01T10:00:00Z"),
                ZoneId.of("UTC")
        );

        limitValidator = new BalanceAndLimitValidator(transactionRepository, dayClock);

        Wallet sender = new Wallet();
        sender.setNightLimit(new BigDecimal("1000.00"));
        sender.setTransactionLimit(new BigDecimal("5000.00"));
        sender.setDailyLimit(new BigDecimal("10000.00"));

        BigDecimal amount = new BigDecimal("2000.00");

        given(transactionRepository.sumDailyUsage(any(), any()))
                .willReturn(BigDecimal.ZERO);

        Assertions.assertDoesNotThrow(() ->
                limitValidator.validate(sender, amount, "1234")
        );
    }

    @Test
    void shouldThrowExceptionWhenTransactionLimitExceeded() {
        limitValidator = new BalanceAndLimitValidator(transactionRepository, Clock.systemDefaultZone());

        Wallet sender = new Wallet();
        sender.setTransactionLimit(new BigDecimal("1000.00"));
        BigDecimal amount = new BigDecimal("1000.01");

        Assertions.assertThrows(LimitExceededException.class, () ->
                limitValidator.validate(sender, amount, "1234")
        );
    }

    @Test
    void shouldThrowExceptionWhenDailyLimitExceeded() {
        limitValidator = new BalanceAndLimitValidator(transactionRepository, Clock.systemDefaultZone());

        Wallet sender = new Wallet();
        sender.setId(UUID.randomUUID());
        sender.setTransactionLimit(new BigDecimal("5000.00"));
        sender.setDailyLimit(new BigDecimal("2000.00"));

        given(transactionRepository.sumDailyUsage(any(), any()))
                .willReturn(new BigDecimal("1900.00"));

        BigDecimal amount = new BigDecimal("101.00");

        Assertions.assertThrows(LimitExceededException.class, () ->
                limitValidator.validate(sender, amount, "1234")
        );
    }
}