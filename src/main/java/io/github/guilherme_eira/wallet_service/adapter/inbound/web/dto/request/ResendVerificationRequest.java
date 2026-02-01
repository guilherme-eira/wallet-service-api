package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para reenviar email de verificação")
public record ResendVerificationRequest(

        @Schema(description = "E-mail cadastrado", example = "guilherme@email.com")
        @NotBlank(message = "O campo 'email' é obrigatório")
        @Email(message = "Deve ser um e-mail válido")
        String email
) {
}