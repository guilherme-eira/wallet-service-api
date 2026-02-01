package io.github.guilherme_eira.wallet_service.application.port.in;

public interface ResendVerificationUseCase {
    void execute(String email);
}
