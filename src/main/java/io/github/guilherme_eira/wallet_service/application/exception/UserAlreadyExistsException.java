package io.github.guilherme_eira.wallet_service.application.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Já existe um usuário ativo com os dados informados");
    }
}
