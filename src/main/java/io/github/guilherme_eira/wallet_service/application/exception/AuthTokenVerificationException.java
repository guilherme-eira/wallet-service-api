package io.github.guilherme_eira.wallet_service.application.exception;

public class AuthTokenVerificationException extends RuntimeException {
    public AuthTokenVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
