package io.github.guilherme_eira.wallet_service.domain.valueobject;

import io.github.guilherme_eira.wallet_service.domain.exception.InvalidPasswordException;

import java.util.Objects;

public class Password {
    private final String rawPassword;

    public Password(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()){
            throw new InvalidPasswordException("A senha não pode ser nula ou vazia");
        }

        if (rawPassword.length() < 8){
            throw new InvalidPasswordException("A senha deve apresentar pelo menos 8 caracteres");
        }

        if (!rawPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d).*$")){
            throw new InvalidPasswordException("A senha deve apresentar pelo menos uma letra e um número");
        }

        this.rawPassword = rawPassword;
    }

    public String getValue(){
        return rawPassword;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Password password = (Password) object;
        return Objects.equals(rawPassword, password.rawPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rawPassword);
    }
}
