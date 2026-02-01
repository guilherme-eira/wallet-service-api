package io.github.guilherme_eira.wallet_service.application.exception;

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException() {
        super("Usuário não verificado");
    }
}
