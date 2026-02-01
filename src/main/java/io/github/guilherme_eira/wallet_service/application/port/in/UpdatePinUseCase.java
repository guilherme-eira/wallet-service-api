package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdatePinCommand;

public interface UpdatePinUseCase {
    void execute(UpdatePinCommand cmd);
}
