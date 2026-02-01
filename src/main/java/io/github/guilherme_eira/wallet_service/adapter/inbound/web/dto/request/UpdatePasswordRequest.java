package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para alteração de senha (logado)")
public record UpdatePasswordRequest(

        @Schema(description = "Senha atual para validação", example = "Antig@Senha1")
        @NotBlank(message = "O campo 'currentPassword' é obrigatório")
        String currentPassword,

        @Schema(description = "Nova senha desejada", example = "Nov4Senh@Forte!")
        @NotBlank(message = "O campo 'newPassword' é obrigatório")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String newPassword
) {
}