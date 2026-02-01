package io.github.guilherme_eira.wallet_service.application.dto.input;

import java.math.BigDecimal;

public record DepositCommand(
        BigDecimal amount,
        String paymentKey
) {
}
