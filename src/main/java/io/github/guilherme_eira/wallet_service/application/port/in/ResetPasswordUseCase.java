package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.ResetPasswordCommand;

public interface ResetPasswordUseCase {
    void execute(ResetPasswordCommand cmd);
}
