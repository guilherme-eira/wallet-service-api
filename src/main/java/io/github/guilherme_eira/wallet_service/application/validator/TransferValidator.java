package io.github.guilherme_eira.wallet_service.application.validator;

import io.github.guilherme_eira.wallet_service.domain.model.Wallet;

import java.math.BigDecimal;

public interface TransferValidator {
    void validate(Wallet sender, Wallet receiver, BigDecimal amount, String pin);
}
