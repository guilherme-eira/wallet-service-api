package io.github.guilherme_eira.wallet_service.application.dto.input;

import java.util.UUID;

public record UpdateUserCommand(
        UUID id,
        String name
) {
}
