package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.MfaSetupCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCurrentPasswordException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DisableMfaServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    DisableMfaService disableMfaService;

    @Test
    void shouldDisableMfaSuccessfullyWhenPasswordIsCorrect() {
        var userId = UUID.randomUUID();
        var currentPassword = "strongPassword123";
        var encodedPassword = "encoded_hash_123";
        var command = new MfaSetupCommand(userId, currentPassword);

        var user = new User();
        user.setId(userId);
        user.setPassword(encodedPassword);
        user.setTwoFactorActive(true);

        BDDMockito.given(repository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(currentPassword, encodedPassword))
                .willReturn(true);

        disableMfaService.execute(command);

        Assertions.assertFalse(user.isTwoFactorActive(), "O MFA deveria ter sido desativado");
        Assertions.assertNotNull(user.getUpdatedAt(), "A data de atualização deveria ser registrada");

        verify(repository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        var userId = UUID.randomUUID();
        var command = new MfaSetupCommand(userId,"wrongPassword");

        var user = new User();
        user.setId(userId);
        user.setPassword("real_hash");
        user.setTwoFactorActive(true);

        BDDMockito.given(repository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(command.currentPassword(), user.getPassword()))
                .willReturn(false);

        Assertions.assertThrows(IncorrectCurrentPasswordException.class, () -> {
            disableMfaService.execute(command);
        });

        Assertions.assertTrue(user.isTwoFactorActive(), "O MFA NÃO deveria ter sido alterado");
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        var command = new MfaSetupCommand(UUID.randomUUID(),"password");

        BDDMockito.given(repository.findById(any()))
                .willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            disableMfaService.execute(command);
        });

        verify(encoder, never()).matches(any(), any());
        verify(repository, never()).save(any());
    }
}