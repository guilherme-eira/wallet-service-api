package io.github.guilherme_eira.wallet_service.application.exception;

public class PasswordResetTokenNotFoundException extends RuntimeException {
    public PasswordResetTokenNotFoundException() {
        super("Token de verificação não encontrado");
    }
}
