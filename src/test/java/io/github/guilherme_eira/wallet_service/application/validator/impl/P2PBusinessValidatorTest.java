package io.github.guilherme_eira.wallet_service.application.validator.impl;

import io.github.guilherme_eira.wallet_service.application.exception.MerchantTransferNotAllowedException;
import io.github.guilherme_eira.wallet_service.application.exception.TransferToSameWalletException;
import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class P2PBusinessValidatorTest {

    @InjectMocks P2PBusinessValidator p2pValidator;

    @Test
    void shouldThrowExceptionWhenMerchantTriesToTransfer() {
        User merchant = new User();
        merchant.setType(UserType.MERCHANT);
        Wallet sender = new Wallet();
        sender.setOwner(merchant);

        Assertions.assertThrows(MerchantTransferNotAllowedException.class, () ->
                p2pValidator.validate(sender, null, BigDecimal.TEN, "1234")
        );
    }

    @Test
    void shouldThrowExceptionWhenTransferToSelf() {
        User common = new User();
        common.setType(UserType.COMMON);

        UUID walletId = UUID.randomUUID();
        Wallet sender = new Wallet();
        sender.setId(walletId); sender.setOwner(common);

        Wallet receiver = new Wallet();
        receiver.setId(walletId);

        Assertions.assertThrows(TransferToSameWalletException.class, () ->
                p2pValidator.validate(sender, receiver, BigDecimal.TEN, "1234")
        );
    }


}