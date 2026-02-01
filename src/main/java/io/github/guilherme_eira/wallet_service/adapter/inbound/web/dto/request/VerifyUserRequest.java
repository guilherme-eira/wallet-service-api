package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Token de verificação de conta")
public record VerifyUserRequest(

        @Schema(description = "Token UUID recebido por e-mail no cadastro", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "O campo 'token' é obrigatório")
        String token
) {
}