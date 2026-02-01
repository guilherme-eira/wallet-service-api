package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.port.in.DeleteUnverifiedUsersUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeleteUnverifiedUsersService implements DeleteUnverifiedUsersUseCase {

    private final UserRepository repository;

    @Override
    @Transactional
    public void execute() {
        var cutOffDate = LocalDateTime.now().minusHours(24);
        repository.deleteByVerifiedFalseAndCreatedAtBefore(cutOffDate);
    }
}
