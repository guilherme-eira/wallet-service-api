package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Informações da Carteira (Saldo e Limites)")
public record WalletResponse(

        @Schema(description = "ID da carteira", example = "a1b2c3d4-e5f6-7890-1234-56789abcdef0")
        UUID walletId,

        @Schema(description = "Saldo disponível para uso imediato", example = "1250.75")
        BigDecimal balance,

        @Schema(description = "Limite máximo por única transação", example = "2000.00")
        BigDecimal transactionLimit,

        @Schema(description = "Limite máximo para transações em horário noturno (20h-06h)", example = "1000.00")
        BigDecimal nightLimit,

        @Schema(description = "Limite acumulado diário (renova à meia-noite)", example = "5000.00")
        BigDecimal dailyLimit
) {
}