package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Comprovante de Transferência P2P")
public record TransferResponse(

        @Schema(description = "ID da transação", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Valor transferido", example = "100.50")
        BigDecimal amount,

        @Schema(description = "Nome de quem recebeu o valor", example = "João da Silva")
        String counterpartyName,

        @Schema(description = "Data e hora da transferência")
        LocalDateTime createdAt
) {
}