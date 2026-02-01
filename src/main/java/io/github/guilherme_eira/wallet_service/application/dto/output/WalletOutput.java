package io.github.guilherme_eira.wallet_service.application.dto.output;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletOutput(
        UUID walletId,
        BigDecimal balance,
        BigDecimal transactionLimit,
        BigDecimal nightLimit,
        BigDecimal dailyLimit
) {
}
