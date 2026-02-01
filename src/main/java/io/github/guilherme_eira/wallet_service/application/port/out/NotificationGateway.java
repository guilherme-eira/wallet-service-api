package io.github.guilherme_eira.wallet_service.application.port.out;

import java.math.BigDecimal;

public interface NotificationGateway {
    void sendVerificationEmail(String email, String token);
    void sendResetPasswordResetEmail(String email, String token);
    void sendTransferReceivedEmail(String email, String senderName, BigDecimal amount);
    void sendTransferSentEmail(String email, String receiverName, BigDecimal amount);
    void sendDepositReceivedEmail(String email, BigDecimal amount);
    void sendWithdrawalSuccessEmail(String email, BigDecimal amount);
}
