package io.github.guilherme_eira.wallet_service.application.dto.output;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterOutput(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt,
        Boolean requiresVerification
) {
}
