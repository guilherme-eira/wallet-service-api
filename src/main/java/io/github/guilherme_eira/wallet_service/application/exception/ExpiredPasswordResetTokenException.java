package io.github.guilherme_eira.wallet_service.application.exception;

public class ExpiredPasswordResetTokenException extends RuntimeException {
    public ExpiredPasswordResetTokenException() {
        super("Token de redefinição de senha expirado");
    }
}
