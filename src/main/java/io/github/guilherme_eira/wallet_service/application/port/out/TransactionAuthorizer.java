package io.github.guilherme_eira.wallet_service.application.port.out;

public interface TransactionAuthorizer {
    Boolean authorizeTransaction();
}
