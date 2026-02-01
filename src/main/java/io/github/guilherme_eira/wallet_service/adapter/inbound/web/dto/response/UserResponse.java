package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Perfil completo do usuário")
public record UserResponse(

        @Schema(description = "ID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome completo", example = "Guilherme Eira")
        String name,

        @Schema(description = "E-mail de contato", example = "guilherme@email.com")
        String email,

        @Schema(description = "CPF/CNPJ cadastrado", example = "123.456.789-00")
        String taxId,

        @Schema(description = "Tipo de conta", example = "COMMON")
        UserType type,

        @Schema(description = "Status da autenticação de dois fatores (2FA)", example = "true")
        Boolean isMfaEnabled
) {
}