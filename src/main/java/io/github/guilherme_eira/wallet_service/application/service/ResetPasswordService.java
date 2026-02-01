package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.ResetPasswordCommand;
import io.github.guilherme_eira.wallet_service.application.exception.ExpiredPasswordResetTokenException;
import io.github.guilherme_eira.wallet_service.application.exception.PasswordResetTokenNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.in.ResetPasswordUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordResetTokenRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.valueobject.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResetPasswordService implements ResetPasswordUseCase {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public void execute(ResetPasswordCommand cmd) {

        var token = passwordResetTokenRepository.findByToken(cmd.resetToken())
                .orElseThrow(PasswordResetTokenNotFoundException::new);

        if (token.isExpired()){
            throw new ExpiredPasswordResetTokenException();
        }

        var user = token.getUser();
        var password = new Password(cmd.newPassword());
        user.setPassword(encoder.encode(password.getValue()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        passwordResetTokenRepository.delete(token);
    }
}
