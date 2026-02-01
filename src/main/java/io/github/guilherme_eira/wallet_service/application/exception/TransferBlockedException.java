package io.github.guilherme_eira.wallet_service.application.exception;

public class TransferBlockedException extends RuntimeException {
    public TransferBlockedException(String message) {
        super(message);
    }
}
