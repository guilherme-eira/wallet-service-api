package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.github.guilherme_eira.wallet_service.domain.enumeration.OperationType;
import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Item de detalhe do histórico de transações (Extrato)")
public record TransactionResponse(

        @Schema(description = "ID único da transação", example = "a1b2c3d4-e5f6-7890-1234-56789abcdef0")
        UUID id,

        @Schema(description = "Tipo da transação (Origem)", example = "TRANSFER")
        TransactionType type,

        @Schema(description = "Tipo da operação (Fluxo financeiro)", example = "DEBIT")
        OperationType operation,

        @Schema(description = "Valor da transação (absoluto)", example = "150.00")
        BigDecimal amount,

        @Schema(description = "Data e hora da ocorrência")
        LocalDateTime date,

        @Schema(description = "Nome da contraparte (Quem recebeu ou enviou)", example = "Mercado Livre Ltda")
        String receiver
) {
}