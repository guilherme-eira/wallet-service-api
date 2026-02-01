package io.github.guilherme_eira.wallet_service.application.exception;

public class IncorrectCredentialsException extends RuntimeException {
    public IncorrectCredentialsException() {
        super("Email ou senha incorretos");
    }
}
