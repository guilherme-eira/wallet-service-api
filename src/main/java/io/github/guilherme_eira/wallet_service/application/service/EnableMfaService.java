package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.MfaSetupCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCurrentPasswordException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.in.EnableMfaUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.MfaProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnableMfaService implements EnableMfaUseCase {

    private final UserRepository repository;
    private final MfaProvider mfaProvider;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public String execute(MfaSetupCommand cmd) {
        var user = repository.findById(cmd.id())
                .orElseThrow(UserNotFoundException::new);

        if (!encoder.matches(cmd.currentPassword(), user.getPassword())) {
            throw new IncorrectCurrentPasswordException();
        }

        var secret = mfaProvider.generateSecret();
        user.enableMfa(secret);
        user.setUpdatedAt(LocalDateTime.now());

        repository.save(user);
        return buildUrl(user.getEmail(), secret);
    }

    private String buildUrl(String email, String secret){
        var issuer = "Wallet Service API";
        return "otpauth://totp/%s:%s?secret=%s&issuer=%s"
                .formatted(issuer, email, secret, issuer);
    }
}
