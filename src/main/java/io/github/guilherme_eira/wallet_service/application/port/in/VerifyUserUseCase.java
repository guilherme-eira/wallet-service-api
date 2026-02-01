package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.output.RegisterOutput;

public interface VerifyUserUseCase {
    RegisterOutput execute(String token);
}
