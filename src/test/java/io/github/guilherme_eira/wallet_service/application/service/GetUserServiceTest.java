package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.UserOutput;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.UserOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserOutputMapper outputMapper;

    @InjectMocks GetUserService service;

    @Test
    void shouldReturnUserWhenExists() {
        var userId = UUID.randomUUID();
        var user = new User();
        var output = new UserOutput(null, null, null, null, null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(outputMapper.toUserOutput(user)).willReturn(output);

        var result = service.execute(userId);

        Assertions.assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        given(userRepository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            service.execute(UUID.randomUUID());
        });
    }
}