package io.github.guilherme_eira.wallet_service.adapter.outbound.integration.client.dto;

import java.math.BigDecimal;

public record BankTransferRequest(
        BigDecimal amount,
        String paymentKey
) {}
