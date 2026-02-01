package io.github.guilherme_eira.wallet_service.application.dto.input;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferCommand(
        UUID senderId,
        String receiverTaxId,
        BigDecimal amount,
        String transactionPin
) {
}
