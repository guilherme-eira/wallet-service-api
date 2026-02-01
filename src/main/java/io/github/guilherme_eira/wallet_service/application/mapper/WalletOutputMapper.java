package io.github.guilherme_eira.wallet_service.application.mapper;

import io.github.guilherme_eira.wallet_service.application.dto.output.WalletOutput;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletOutputMapper {

    public WalletOutput toWalletOutput(Wallet wallet){
        return new WalletOutput(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getTransactionLimit(),
                wallet.getNightLimit(),
                wallet.getDailyLimit()
        );
    }
}
