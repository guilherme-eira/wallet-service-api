package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados do usuário recém-criado")
public record RegisterResponse(

        @Schema(description = "ID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome cadastrado", example = "Guilherme Eira")
        String name,

        @Schema(description = "E-mail cadastrado", example = "guilherme@email.com")
        String email,

        @Schema(description = "Data de criação")
        LocalDateTime createdAt,

        @Schema(description = "Indica se a verificação de e-mail está pendente", example = "true")
        Boolean requiresVerification
) {
}