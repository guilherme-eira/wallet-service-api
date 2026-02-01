package io.github.guilherme_eira.wallet_service.application.port.in;

import java.util.UUID;

public interface DeleteUserUseCase {
    void execute(UUID id);
}
