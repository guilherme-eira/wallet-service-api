package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para transferência interna (P2P)")
public record TransferRequest(

        @Schema(description = "Chave do recebedor (CPF, Email ou ID)", example = "605.804.810-93")
        @NotBlank(message = "O campo 'receiver' é obrigatório")
        String receiver,

        @Schema(description = "Valor a ser transferido", example = "100.00")
        @NotNull(message = "O campo 'amount' é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser positivo")
        BigDecimal amount,

        @Schema(description = "PIN de transação (4 dígitos)", example = "1246")
        @NotBlank(message = "O campo 'transactionPin' é obrigatório")
        @Size(min = 4, max = 4, message = "O PIN deve ter 4 dígitos")
        String transactionPin
) {
}