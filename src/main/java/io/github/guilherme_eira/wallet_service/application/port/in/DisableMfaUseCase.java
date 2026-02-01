package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.MfaSetupCommand;

public interface DisableMfaUseCase {
    void execute(MfaSetupCommand cmd);
}
