package io.github.guilherme_eira.wallet_service.application.exception;

public class MerchantTransferNotAllowedException extends RuntimeException {
    public MerchantTransferNotAllowedException() {
        super("Um lojista só pode receber transferências");
    }
}
