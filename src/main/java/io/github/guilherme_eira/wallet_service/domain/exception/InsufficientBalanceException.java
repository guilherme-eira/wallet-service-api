package io.github.guilherme_eira.wallet_service.domain.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Não é possível debitar este valor pois ele é inferior ao saldo\"");
    }
}
