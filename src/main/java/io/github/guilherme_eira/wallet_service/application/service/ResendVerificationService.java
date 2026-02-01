package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.port.in.ResendVerificationUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.NotificationGateway;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.VerificationTokenRepository;
import io.github.guilherme_eira.wallet_service.domain.model.VerificationToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResendVerificationService implements ResendVerificationUseCase {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final NotificationGateway notificationGateway;

    @Override
    @Transactional
    public void execute(String email) {

        try {
            var userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                log.warn("Reenvio de verificação solicitado para email inexistente: {}", email);
                return;
            }

            var user = userOptional.get();

            if (user.isVerified()) {
                log.info("Reenvio ignorado. Usuário já verificado: {}", email);
                return;
            }

            var token = verificationTokenRepository.findByUser(user);

            if (token.isPresent()) {
                var existingToken = token.get();
                if (existingToken.isResendBlocked()) {
                    log.warn("Envio ignorado por Rate Limit (Muitas tentativas): {}", email);
                    return;
                }

                verificationTokenRepository.delete(existingToken);
                verificationTokenRepository.flush();
            }

            var newToken = VerificationToken.create(user);
            verificationTokenRepository.save(newToken);

            notificationGateway.sendVerificationEmail(email, newToken.getToken());

        } catch(Exception ex){
            log.error("Ocorreu um erro no envio do email de verificação: {}", email);
        }
    }
}
