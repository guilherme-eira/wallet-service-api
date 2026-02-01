package io.github.guilherme_eira.wallet_service.application.dto.input;

public record LoginCommand(
        String email,
        String password
) {
}
