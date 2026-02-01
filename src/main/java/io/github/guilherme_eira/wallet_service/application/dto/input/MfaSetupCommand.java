package io.github.guilherme_eira.wallet_service.application.dto.input;

import java.util.UUID;

public record MfaSetupCommand(
        UUID id,
        String currentPassword
) {
}
