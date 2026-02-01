package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para realização de saque externo")
public record WithdrawRequest(

        @Schema(description = "Valor a ser sacado", example = "500.00")
        @NotNull(message = "O campo 'amount' é obrigatório")
        @DecimalMin(value = "0.01", message = "'amount' deve ser maior que zero")
        BigDecimal amount,

        @Schema(description = "PIN de transação para autorizar o saque", example = "1246")
        @NotBlank(message = "O campo 'transactionPin' é obrigatório")
        @Size(min = 4, max = 4, message = "O PIN deve ter 4 dígitos")
        String transactionPin,

        @Schema(description = "CPF de destino (Chave de Pagamento)", example = "605.804.810-93")
        @NotBlank(message = "O campo 'paymentKey' é obrigatório")
        String paymentKey
) {
}