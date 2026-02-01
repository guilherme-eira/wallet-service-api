package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.port.in.ForgotPasswordUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.NotificationGateway;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordResetTokenRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.PasswordResetToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordService implements ForgotPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final NotificationGateway notificationGateway;

    @Override
    @Transactional
    public void execute(String email) {
        try {
            var userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                log.warn("Envio de token solicitado para email inexistente: {}", email);
                return;
            }

            var user = userOptional.get();

            if (!user.isVerified()) {
                log.info("Envio ignorado. Usuário não está verificado: {}", email);
                return;
            }

            var token = passwordResetTokenRepository.findByUser(user);

            if (token.isPresent()){
                var existingToken = token.get();
                if (existingToken.isResendBlocked()) {
                    log.warn("Envio ignorado por Rate Limit (Muitas tentativas): {}", email);
                    return;
                }

                passwordResetTokenRepository.delete(existingToken);
                passwordResetTokenRepository.flush();
            }

            var newToken = PasswordResetToken.create(user);
            passwordResetTokenRepository.save(newToken);

            notificationGateway.sendResetPasswordResetEmail(email,newToken.getToken());

        } catch(Exception ex){
            log.warn("Ocorreu um erro no envio do email de redefinição de senha: {}", ex.getMessage());
        }
    }
}
