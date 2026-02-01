package io.github.guilherme_eira.wallet_service.application.dto.output;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateUserOutput(
        UUID id,
        String name,
        String email,
        LocalDateTime updatedAt
) {
}
