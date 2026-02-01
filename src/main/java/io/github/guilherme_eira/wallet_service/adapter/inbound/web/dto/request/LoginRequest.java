package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais de acesso")
public record LoginRequest(

        @Schema(description = "E-mail do usuário", example = "guilherme@email.com")
        @NotBlank(message = "O campo 'email' é obrigatório")
        @Email(message = "Deve ser um e-mail válido")
        String email,

        @Schema(description = "Senha de acesso", example = "S3nhaForte!123")
        @NotBlank(message = "O campo 'password' é obrigatório")
        String password
) {
}