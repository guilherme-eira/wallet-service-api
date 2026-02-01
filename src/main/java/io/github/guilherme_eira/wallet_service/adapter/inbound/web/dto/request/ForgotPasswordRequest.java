package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para iniciar recuperação de senha")
public record ForgotPasswordRequest(

        @Schema(description = "E-mail cadastrado na conta", example = "guilherme@email.com")
        @NotBlank(message = "O campo 'email' é obrigatório")
        @Email(message = "Deve ser um e-mail válido")
        String email
) {
}