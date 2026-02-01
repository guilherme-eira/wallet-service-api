package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.LoginCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;

public interface LoginUseCase {
    AuthOutput execute(LoginCommand cmd);
}
