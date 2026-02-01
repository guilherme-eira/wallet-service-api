package io.github.guilherme_eira.wallet_service.application.port.out;

import java.math.BigDecimal;

public interface BankTransferGateway {
    boolean transferFunds(BigDecimal amount, String paymentKey);
}
