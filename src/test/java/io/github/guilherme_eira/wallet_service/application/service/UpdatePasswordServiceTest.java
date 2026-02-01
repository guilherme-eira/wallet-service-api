package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdatePasswordCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCurrentPasswordException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePasswordServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder encoder;

    @InjectMocks UpdatePasswordService service;

    @Test
    void shouldUpdatePasswordSuccessfullyWhenCurrentPasswordIsCorrect() {
        // Cenário
        var userId = UUID.randomUUID();
        var currentPass = "OldPass123!";
        var newPassRaw = "NewStrongPass123!";
        var newPassHash = "encoded_new_hash";

        var command = new UpdatePasswordCommand(userId, currentPass, newPassRaw);

        var user = new User();
        user.setId(userId);
        user.setPassword("old_hash");

        BDDMockito.given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(currentPass, "old_hash"))
                .willReturn(true);

        BDDMockito.given(encoder.encode(newPassRaw))
                .willReturn(newPassHash);

        service.execute(command);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        Assertions.assertEquals(newPassHash, savedUser.getPassword());
        Assertions.assertNotEquals("old_hash", savedUser.getPassword());
        Assertions.assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        var userId = UUID.randomUUID();
        var command = new UpdatePasswordCommand(userId, "WrongPass", "NewPass123!");

        var user = new User();
        user.setPassword("real_hash");

        BDDMockito.given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(command.currentPassword(), user.getPassword()))
                .willReturn(false);

        Assertions.assertThrows(IncorrectCurrentPasswordException.class, () -> {
            service.execute(command);
        });

        Assertions.assertEquals("real_hash", user.getPassword());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        var command = new UpdatePasswordCommand(UUID.randomUUID(), "Pass", "NewPass");

        BDDMockito.given(userRepository.findById(any()))
                .willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            service.execute(command);
        });

        verify(encoder, never()).matches(any(), any());
        verify(userRepository, never()).save(any());
    }
}