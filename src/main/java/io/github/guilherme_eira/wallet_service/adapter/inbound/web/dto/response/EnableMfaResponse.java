package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para configuração do 2FA")
public record EnableMfaResponse(

        @Schema(description = "URL padrão 'otpauth' para geração de QR Code", example = "otpauth://totp/WalletService:guilherme@email.com?secret=JBSWY3DPEHPK3PXP&issuer=WalletService")
        String url
) {
}