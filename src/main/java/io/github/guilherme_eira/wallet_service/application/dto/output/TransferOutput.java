package io.github.guilherme_eira.wallet_service.application.dto.output;

import io.github.guilherme_eira.wallet_service.domain.enumeration.OperationType;
import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferOutput(
        UUID id,
        BigDecimal amount,
        String counterpartyName,
        LocalDateTime createdAt
) {
}
