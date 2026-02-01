package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para redefinição de senha (esqueci minha senha)")
public record ResetPasswordRequest(

        @Schema(description = "Token UUID recebido por email", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "O campo 'token' é obrigatório")
        String token,

        @Schema(description = "Nova senha forte", example = "Nov4Senh@Forte!")
        @NotBlank(message = "O campo 'newPassword' é obrigatório")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String newPassword
) {
}