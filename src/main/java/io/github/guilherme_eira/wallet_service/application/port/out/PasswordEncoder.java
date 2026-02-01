package io.github.guilherme_eira.wallet_service.application.port.out;

public interface PasswordEncoder {
    String encode(String rawPassword);
    Boolean matches(String rawPassword, String hashedPassword);
}
