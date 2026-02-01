package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdateUserCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.UpdateUserOutput;

public interface UpdateUserUseCase {
    UpdateUserOutput execute(UpdateUserCommand cmd);
}
