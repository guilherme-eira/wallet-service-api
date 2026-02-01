package io.github.guilherme_eira.wallet_service.adapter.outbound.integration.client.dto;

public record BankTransferResponse(
        String status,
        String bankTransactionId
) {}
