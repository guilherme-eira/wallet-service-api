package io.github.guilherme_eira.wallet_service.application.validator.impl;

import io.github.guilherme_eira.wallet_service.application.exception.MerchantTransferNotAllowedException;
import io.github.guilherme_eira.wallet_service.application.exception.TransferToSameWalletException;
import io.github.guilherme_eira.wallet_service.application.validator.TransferValidator;
import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(3)
public class P2PBusinessValidator implements TransferValidator {

    @Override
    public void validate(Wallet sender, Wallet receiver, BigDecimal amount, String pin) {

        if (sender.getOwner().getType() == UserType.MERCHANT) {
            throw new MerchantTransferNotAllowedException();
        }

        if (sender.getId().equals(receiver.getId())) {
            throw new TransferToSameWalletException();
        }
    }
}
