package io.github.guilherme_eira.wallet_service.application.exception;

public class LoginBlockedException extends RuntimeException {
    public LoginBlockedException(String message) {
        super(message);
    }
}
