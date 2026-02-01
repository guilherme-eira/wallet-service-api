package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.UserOutput;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.UserOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.GetUserUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserService implements GetUserUseCase {

    private final UserRepository userRepository;
    private final UserOutputMapper outputMapper;

    @Override
    public UserOutput execute(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return outputMapper.toUserOutput(user);
    }
}
