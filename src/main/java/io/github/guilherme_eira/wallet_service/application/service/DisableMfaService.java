package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.MfaSetupCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCurrentPasswordException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.in.DisableMfaUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DisableMfaService implements DisableMfaUseCase {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void execute(MfaSetupCommand cmd) {
        var user = repository.findById(cmd.id())
                .orElseThrow(UserNotFoundException::new);

        if (!encoder.matches(cmd.currentPassword(), user.getPassword())) {
            throw new IncorrectCurrentPasswordException();
        }

        user.disableMfa();
        user.setUpdatedAt(LocalDateTime.now());

        repository.save(user);
    }
}
