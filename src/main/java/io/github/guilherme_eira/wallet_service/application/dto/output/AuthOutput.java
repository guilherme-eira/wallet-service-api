package io.github.guilherme_eira.wallet_service.application.dto.output;

public record AuthOutput(
        String token,
        String refreshToken,
        boolean requiresMfa
) {
}
