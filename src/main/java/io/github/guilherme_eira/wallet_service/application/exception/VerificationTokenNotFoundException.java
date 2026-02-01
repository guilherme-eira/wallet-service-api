package io.github.guilherme_eira.wallet_service.application.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException() {
        super("Token de verificação não encontrado");
    }
}
