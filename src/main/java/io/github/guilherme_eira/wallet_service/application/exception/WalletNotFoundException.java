package io.github.guilherme_eira.wallet_service.application.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException() {
        super("Carteira não encontrada");
    }
}
