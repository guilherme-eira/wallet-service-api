package io.github.guilherme_eira.wallet_service.application.exception;

public class TransactionNotAuthorizedException extends RuntimeException {
    public TransactionNotAuthorizedException(String message) {
        super(message);
    }
}
