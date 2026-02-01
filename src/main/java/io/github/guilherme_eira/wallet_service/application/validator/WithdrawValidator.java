package io.github.guilherme_eira.wallet_service.application.validator;

import io.github.guilherme_eira.wallet_service.domain.model.Wallet;

import java.math.BigDecimal;

public interface WithdrawValidator {
    void validate(Wallet wallet, BigDecimal amount, String pin);
}
