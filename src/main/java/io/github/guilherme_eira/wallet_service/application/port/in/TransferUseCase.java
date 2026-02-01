package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.input.TransferCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.TransferOutput;

public interface TransferUseCase {
    TransferOutput execute(TransferCommand cmd);
}
