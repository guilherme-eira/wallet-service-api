package io.github.guilherme_eira.wallet_service.application.exception;

public class AuthTokenGenerationException extends RuntimeException {
    public AuthTokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
