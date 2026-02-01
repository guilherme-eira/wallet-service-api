package io.github.guilherme_eira.wallet_service.application.dto.input;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawCommand(
        UUID id,
        BigDecimal amount,
        String transactionPin,
        String pixKey
) {
}
