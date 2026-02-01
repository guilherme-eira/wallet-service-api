package io.github.guilherme_eira.wallet_service.application.exception;

public class IncorrectPinException extends RuntimeException {
    public IncorrectPinException() {
        super("Pin de transação incorreto");
    }
}
