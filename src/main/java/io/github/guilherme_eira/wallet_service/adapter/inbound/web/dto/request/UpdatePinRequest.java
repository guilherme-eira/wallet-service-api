package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para troca de PIN de transação")
public record UpdatePinRequest(

        @Schema(description = "Senha de login atual (para garantir que é o dono da conta)", example = "S3nhaAntiga!123")
        @NotBlank(message = "O campo 'currentPassword' é obrigatório")
        String currentPassword,

        @Schema(description = "Novo PIN numérico de 4 dígitos", example = "9090")
        @NotBlank(message = "O campo 'newPin' é obrigatório")
        @Size(min = 4, max = 4, message = "O PIN deve ter exatamente 4 dígitos")
        String newPin
) {
}