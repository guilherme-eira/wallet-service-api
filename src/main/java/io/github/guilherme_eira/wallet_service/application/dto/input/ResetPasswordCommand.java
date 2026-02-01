package io.github.guilherme_eira.wallet_service.application.dto.input;

public record ResetPasswordCommand(
        String resetToken,
        String newPassword
) {
}
