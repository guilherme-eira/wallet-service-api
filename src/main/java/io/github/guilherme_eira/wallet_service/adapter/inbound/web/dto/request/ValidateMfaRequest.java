package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para validação de código 2FA")
public record ValidateMfaRequest(

        @Schema(description = "Código TOTP de 6 dígitos gerado pelo app autenticador", example = "123456")
        @NotBlank(message = "O campo 'code' é obrigatório")
        @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos")
        String code
) {
}