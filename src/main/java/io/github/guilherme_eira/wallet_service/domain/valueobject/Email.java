package io.github.guilherme_eira.wallet_service.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private final String address;

    private static final String MAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern PATTERN = Pattern.compile(MAIL_PATTERN);

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("E-mail não pode ser nulo");
        }
        if (!PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Formato de e-mail inválido: " + address);
        }
        this.address = address;
    }

    public String getValue() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return address;
    }
}