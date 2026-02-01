package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.WalletOutput;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.WalletOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.GetWalletUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetWalletService implements GetWalletUseCase {

    private final WalletRepository walletRepository;
    private final WalletOutputMapper outputMapper;

    @Override
    public WalletOutput execute(UUID id) {
        var wallet = walletRepository.findByUserId(id)
                .orElseThrow(WalletNotFoundException::new);

        return outputMapper.toWalletOutput(wallet);
    }
}
