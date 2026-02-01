package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.RegisterCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.RegisterOutput;

public interface RegisterUseCase {
    RegisterOutput execute(RegisterCommand cmd);
}
