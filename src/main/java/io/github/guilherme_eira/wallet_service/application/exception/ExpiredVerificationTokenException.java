package io.github.guilherme_eira.wallet_service.application.exception;

public class ExpiredVerificationTokenException extends RuntimeException {
    public ExpiredVerificationTokenException() {
        super("Token de verificação expirado");
    }
}
