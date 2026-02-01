package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Comprovante de Depósito")
public record DepositResponse(

        @Schema(description = "ID único da transação gerada", example = "a1b2c3d4-e5f6-7890-1234-56789abcdef0")
        UUID id,

        @Schema(description = "Valor creditado na carteira", example = "1500.50")
        BigDecimal amount,

        @Schema(description = "Data e hora do processamento")
        LocalDateTime createdAt
) {
}