package io.github.guilherme_eira.wallet_service.application.exception;

public class NonZeroBalanceException extends RuntimeException {
    public NonZeroBalanceException() {
        super("Não é possível excluir essa conta pois ela apresenta saldo diferente de zero");
    }
}
