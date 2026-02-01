package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdatePasswordCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCurrentPasswordException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.in.UpdatePasswordUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.valueobject.Password;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdatePasswordService implements UpdatePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void execute(UpdatePasswordCommand cmd) {
        var user = userRepository.findById(cmd.id())
                .orElseThrow(UserNotFoundException::new);

        if(!encoder.matches(cmd.currentPassword(), user.getPassword())){
            throw new IncorrectCurrentPasswordException();
        }

        var newPassword = new Password(cmd.newPassword());
        user.setPassword(encoder.encode(newPassword.getValue()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}
