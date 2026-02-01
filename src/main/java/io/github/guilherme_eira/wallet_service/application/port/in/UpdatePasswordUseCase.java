package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdatePasswordCommand;

public interface UpdatePasswordUseCase {
    void execute(UpdatePasswordCommand cmd);
}
