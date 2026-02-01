package io.github.guilherme_eira.wallet_service.application.exception;

public class IncorrectCurrentPasswordException extends RuntimeException {
    public IncorrectCurrentPasswordException() {
        super("A senha informada não corresponde à senha verdadeira");
    }
}
