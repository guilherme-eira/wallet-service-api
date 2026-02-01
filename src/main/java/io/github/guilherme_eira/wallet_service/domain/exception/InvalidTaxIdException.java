package io.github.guilherme_eira.wallet_service.domain.exception;

public class InvalidTaxIdException extends RuntimeException {
    public InvalidTaxIdException(String message) {
        super(message);
    }
}
