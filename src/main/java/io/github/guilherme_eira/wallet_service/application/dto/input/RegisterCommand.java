package io.github.guilherme_eira.wallet_service.application.dto.input;

import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;

public record RegisterCommand(
        String name,
        String taxId,
        String email,
        String password,
        UserType type,
        String transactionPin
) {
}
