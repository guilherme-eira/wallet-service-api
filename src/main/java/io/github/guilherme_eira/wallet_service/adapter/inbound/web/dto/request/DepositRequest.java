package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Payload de notificação de depósito (Webhook)")
public record DepositRequest(

        @Schema(description = "Valor monetário a ser creditado", example = "1500.50")
        @NotNull(message = "O campo 'amount' é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal amount,

        @Schema(description = "CPF do usuário de destino", example = "605.804.810-93")
        @NotBlank(message = "O campo 'paymentKey' é obrigatório")
        String paymentKey
){}