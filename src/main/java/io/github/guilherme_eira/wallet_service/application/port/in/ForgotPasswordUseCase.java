package io.github.guilherme_eira.wallet_service.application.port.in;

public interface ForgotPasswordUseCase {
    void execute(String email);
}
