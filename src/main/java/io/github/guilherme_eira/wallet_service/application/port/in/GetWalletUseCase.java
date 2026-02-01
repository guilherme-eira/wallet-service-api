package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.output.WalletOutput;

import java.util.UUID;

public interface GetWalletUseCase {
    WalletOutput execute(UUID id);
}
