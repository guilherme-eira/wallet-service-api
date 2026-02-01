package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição de renovação de token")
public record RefreshTokenRequest(

        @Schema(description = "Refresh Token (UUID) recebido no login anterior", example = "d9b2d63d-a233-4123-b673-56ce8c8a0012")
        @NotBlank(message = "O campo 'refreshToken' é obrigatório")
        String refreshToken
){
}