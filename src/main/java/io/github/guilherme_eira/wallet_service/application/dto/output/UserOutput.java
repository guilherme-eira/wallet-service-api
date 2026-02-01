package io.github.guilherme_eira.wallet_service.application.dto.output;

import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;

import java.util.UUID;

public record UserOutput(
        UUID id,
        String name,
        String email,
        String taxId,
        UserType type,
        Boolean isMfaEnabled
) {
}
