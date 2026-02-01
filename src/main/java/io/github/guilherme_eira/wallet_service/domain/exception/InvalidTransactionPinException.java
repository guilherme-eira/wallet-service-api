package io.github.guilherme_eira.wallet_service.domain.exception;

public class InvalidTransactionPinException extends RuntimeException {
    public InvalidTransactionPinException(String message) {
        super(message);
    }
}
