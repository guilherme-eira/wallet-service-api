package io.github.guilherme_eira.wallet_service.application.port.out;

public interface MfaProvider {
    String generateSecret();
    Boolean validateMfaCode(String enteredCode, String userSecret);
}
