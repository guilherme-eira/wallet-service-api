package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados atualizados do usuário")
public record UpdateUserResponse(

        @Schema(description = "ID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome atualizado", example = "Guilherme Eira da Silva")
        String name,

        @Schema(description = "E-mail cadastrado", example = "guilherme@email.com")
        String email,

        @Schema(description = "Data da última atualização")
        LocalDateTime updatedAt
) {
}