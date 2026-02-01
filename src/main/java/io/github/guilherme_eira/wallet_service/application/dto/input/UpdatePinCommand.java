package io.github.guilherme_eira.wallet_service.application.dto.input;

import java.util.UUID;

public record UpdatePinCommand(
        UUID id,
        String currentPassword,
        String newPin
) {
}
