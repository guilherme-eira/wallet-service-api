package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.output.UserOutput;

import java.util.UUID;

public interface GetUserUseCase {
    UserOutput execute(UUID id);
}
