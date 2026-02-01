package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para ativar/desativar MFA")
public record MfaSetupRequest(

        @Schema(description = "Senha atual para confirmar a operação de segurança", example = "S3nhaForte!123")
        @NotBlank(message = "O campo 'currentPassword' é obrigatório")
        String currentPassword
) {
}