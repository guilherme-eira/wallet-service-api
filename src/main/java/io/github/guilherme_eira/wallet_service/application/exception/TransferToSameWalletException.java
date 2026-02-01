package io.github.guilherme_eira.wallet_service.application.exception;

public class TransferToSameWalletException extends RuntimeException {
    public TransferToSameWalletException() {
        super("Não é possível realizar uma transferência para a própria carteira");
    }
}
