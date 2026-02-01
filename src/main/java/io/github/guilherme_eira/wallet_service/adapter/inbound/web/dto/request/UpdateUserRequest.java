package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização de perfil")
public record UpdateUserRequest(

        @Schema(description = "Novo nome", example = "Guilherme Augusto")
        @NotBlank(message = "O campo 'name' é obrigatório")
        @Size(min = 3, message = "O nome deve ter pelo menos 3 caracteres")
        String name
) {
}