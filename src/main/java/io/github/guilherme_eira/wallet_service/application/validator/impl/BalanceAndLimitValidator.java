package io.github.guilherme_eira.wallet_service.application.validator.impl;

import io.github.guilherme_eira.wallet_service.application.exception.LimitExceededException;
import io.github.guilherme_eira.wallet_service.application.port.out.TransactionRepository;
import io.github.guilherme_eira.wallet_service.application.validator.TransferValidator;
import io.github.guilherme_eira.wallet_service.application.validator.WithdrawValidator;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Order(2)
public class BalanceAndLimitValidator implements TransferValidator, WithdrawValidator {

    private final TransactionRepository repository;
    private final Clock clock;

    @Override
    public void validate(Wallet sender, Wallet receiver, BigDecimal amount, String pin) {
        validateBalanceAndLimits(sender, amount);
    }

    @Override
    public void validate(Wallet sender, BigDecimal amount, String pin) {
        validateBalanceAndLimits(sender, amount);
    }

    private void validateBalanceAndLimits(Wallet sender, BigDecimal amount) {
        LocalTime now = LocalTime.now(clock);
        boolean isNightTime = now.isAfter(LocalTime.of(20, 0)) || now.isBefore(LocalTime.of(6, 0));

        BigDecimal nightLimit = sender.getNightLimit() != null ? sender.getNightLimit() : new BigDecimal("1000.00");

        if (isNightTime && amount.compareTo(nightLimit) > 0) {
            throw new LimitExceededException("Horário noturno: Limite máximo de R$ " + nightLimit);
        }

        if (amount.compareTo(sender.getTransactionLimit()) > 0) {
            throw new LimitExceededException("Valor excede o limite por transação de R$ " + sender.getTransactionLimit());
        }

        BigDecimal currentSpent = repository.sumDailyUsage(
                sender.getId(),
                LocalDate.now().atStartOfDay()
        );

        if (currentSpent.add(amount).compareTo(sender.getDailyLimit()) > 0) {
            throw new LimitExceededException("Limite diário excedido. Disponível: " + sender.getDailyLimit().subtract(currentSpent));
        }
    }
}
