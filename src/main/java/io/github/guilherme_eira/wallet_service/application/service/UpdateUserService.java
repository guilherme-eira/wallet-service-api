package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdateUserCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.UpdateUserOutput;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.UserOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.UpdateUserUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepository repository;
    private final UserOutputMapper mapper;

    @Override
    @Transactional
    public UpdateUserOutput execute(UpdateUserCommand cmd) {
        var user = repository.findById(cmd.id())
                .orElseThrow(UserNotFoundException::new);

        boolean hasChanges = false;

        if (cmd.name() != null && !cmd.name().equals(user.getName())) {
            user.setName(cmd.name());
            hasChanges = true;
        }

        if (hasChanges) {
            user.setUpdatedAt(LocalDateTime.now());
            var updatedUser = repository.save(user);
            return mapper.toUpdateUserOutput(updatedUser);
        }

        return mapper.toUpdateUserOutput(user);
    }
}
