package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação contendo tokens e estado do MFA")
public record AuthResponse(

        @Schema(description = "Access Token (JWT) de curta duração", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Refresh Token (UUID ou Opaco) de longa duração", example = "d9b2d63d-a233-4123-b673-56ce8c8a0012")
        String refreshToken,

        @Schema(description = "Indica se o usuário precisa validar o MFA. Se true, o token acima é parcial (PRE_AUTH).", example = "false")
        boolean requiresMfa
) {
}