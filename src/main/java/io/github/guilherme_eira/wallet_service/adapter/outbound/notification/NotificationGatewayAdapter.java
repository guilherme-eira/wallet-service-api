package io.github.guilherme_eira.wallet_service.adapter.outbound.notification;

import io.github.guilherme_eira.wallet_service.application.port.out.NotificationGateway;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class NotificationGatewayAdapter implements NotificationGateway {

    private final JavaMailSender mailSender;
    private final Locale localeBR = Locale.forLanguageTag("pt-BR");

    @Override
    @Async
    public void sendVerificationEmail(String email, String token) {
        String link = "http://localhost:8080/auth/verify?token=" + token;

        String body = """
                <p>Seja bem-vindo ao <strong>Wallet Service</strong>!</p>
                <p>Para ativar sua conta, copie o token abaixo e utilize no endpoint de verificação:</p>
                
                <div class="token-box">
                    %s
                </div>
                
                <p>Este código é válido por 24 horas.</p>
                """.formatted(token);

        String content = getHtmlTemplate("Ative sua conta", body);

        sendEmail(email, "Verificação de Email", content);
    }

    @Override
    @Async
    public void sendResetPasswordResetEmail(String email, String token) {
        String link = "http://localhost:8080/auth/reset-password?token=" + token;

        String body = """
                <p>Recebemos uma solicitação para redefinir a senha da sua conta no <strong>Wallet Service</strong>.</p>
                <p>Se foi você, copie o token abaixo e utilize no endpoint de redefinição de senha:</p>
                
                <div class="token-box">
                    %s
                </div>
                
                <p>Se você não solicitou isso, pode ignorar este e-mail com segurança.</p>
                """.formatted(token);

        String content = getHtmlTemplate("Recuperação de Senha", body);

        sendEmail(email, "Redefinição de Senha", content);
    }

    @Override
    @Async
    public void sendTransferReceivedEmail(String email, String senderName, BigDecimal amount) {
        String formattedAmount = NumberFormat.getCurrencyInstance(localeBR).format(amount);

        String body = """
                <p>Olá!</p>
                <p>Você recebeu uma transferência de <strong>%s</strong>.</p>
                
                <div class="amount-box" style="color: #28a745;">
                    + %s
                </div>
                
                <p>O valor já está disponível no seu saldo.</p>
                """.formatted(senderName, formattedAmount);

        String content = getHtmlTemplate("Transferência Recebida", body);
        sendEmail(email, "Você recebeu dinheiro!", content);
    }

    @Override
    @Async
    public void sendTransferSentEmail(String email, String receiverName, BigDecimal amount) {
        String formattedAmount = NumberFormat.getCurrencyInstance(localeBR).format(amount);

        String body = """
                <p>Olá!</p>
                <p>Sua transferência para <strong>%s</strong> foi realizada com sucesso.</p>
                
                <div class="amount-box" style="color: #333;">
                    - %s
                </div>
                
                <p>Este e-mail serve como seu comprovante de transação.</p>
                """.formatted(receiverName, formattedAmount);

        String content = getHtmlTemplate("Comprovante de Envio", body);
        sendEmail(email, "Transferência Enviada", content);
    }

    @Override
    @Async
    public void sendDepositReceivedEmail(String email, BigDecimal amount) {
        String formattedAmount = NumberFormat.getCurrencyInstance(localeBR).format(amount);

        String body = """
            <p>Olá!</p>
            <p>Recebemos a confirmação de um depósito e o valor já está disponível na sua Wallet.</p>
            
            <div class="amount-box" style="color: #28a745;">
                + %s
            </div>
            
            <p>Seu saldo já foi atualizado.</p>
            """.formatted(formattedAmount);

        String content = getHtmlTemplate("Depósito Recebido", body);
        sendEmail(email, "Você recebeu um depósito", content);
    }

    @Override
    @Async
    public void sendWithdrawalSuccessEmail(String email, BigDecimal amount) {
        String formattedAmount = NumberFormat.getCurrencyInstance(localeBR).format(amount);

        String body = """
            <p>Olá!</p>
            <p>Sua transferência para conta externa foi processada com sucesso.</p>
            
            <div class="amount-box" style="color: #333;">
                - %s
            </div>
            
            <p>O valor foi enviado para o banco de destino e deve estar disponível em instantes.</p>
            """.formatted(formattedAmount);

        String content = getHtmlTemplate("Saque Realizado", body);
        sendEmail(email, "Comprovante de Transferência Externa", content);
    }

    private void sendEmail(String email, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@walletservice.com");
            helper.setTo(email);
            helper.setSubject(subject + " | Wallet Service");

            helper.setText(content, true);
            mailSender.send(message);

        } catch (MessagingException ex) {
            System.err.println("Não foi possível enviar email para " + email);
        }
    }

    private String getHtmlTemplate(String title, String contentBody) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
                        .header { background-color: #0056b3; color: white; padding: 10px; text-align: center; border-radius: 8px 8px 0 0; }
                        .content { padding: 20px; color: #333333; line-height: 1.6; }
                
                        .token-box { 
                            background-color: #f8f9fa; 
                            border: 2px dashed #0056b3; 
                            color: #333; 
                            padding: 15px; 
                            font-size: 20px; 
                            font-family: 'Courier New', Courier, monospace; 
                            text-align: center; 
                            letter-spacing: 2px; 
                            font-weight: bold; 
                            margin: 20px 0; 
                            border-radius: 5px; 
                        }
                
                        .footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; border-top: 1px solid #ddd; padding-top: 10px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>%s</h2>
                        </div>
                        <div class="content">
                            %s
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Wallet Service.</p>
                            <p>Não responda a este e-mail.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(title, contentBody);
    }
}
