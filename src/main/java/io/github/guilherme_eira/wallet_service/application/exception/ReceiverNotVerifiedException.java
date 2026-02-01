package io.github.guilherme_eira.wallet_service.application.exception;

public class ReceiverNotVerifiedException extends RuntimeException {
    public ReceiverNotVerifiedException() {
        super("Usuário não verificado");
    }
}
