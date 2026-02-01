package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.WithdrawCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.WithdrawOutput;

public interface WithdrawUseCase {
    WithdrawOutput execute(WithdrawCommand cmd);
}
