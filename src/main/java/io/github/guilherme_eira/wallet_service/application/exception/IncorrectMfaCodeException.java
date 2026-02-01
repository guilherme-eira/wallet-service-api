package io.github.guilherme_eira.wallet_service.application.exception;

public class IncorrectMfaCodeException extends RuntimeException {
    public IncorrectMfaCodeException() {
        super("O código informado para segundo fator não é válido");
    }
}
