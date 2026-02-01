package io.github.guilherme_eira.wallet_service.domain.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException() {
        super("Não é possível adicionar este valor pois ele é inferior ou igual a zero");
    }
}
