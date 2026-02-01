package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.DepositCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.DepositOutput;

public interface DepositUseCase {
    DepositOutput execute(DepositCommand cmd);
}
