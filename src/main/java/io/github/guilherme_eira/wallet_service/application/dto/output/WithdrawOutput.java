package io.github.guilherme_eira.wallet_service.application.dto.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WithdrawOutput(
        UUID id,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
