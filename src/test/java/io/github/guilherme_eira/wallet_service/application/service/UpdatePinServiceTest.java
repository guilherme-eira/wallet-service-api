package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdatePinCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCurrentPasswordException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
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
class UpdatePinServiceTest {

    @Mock UserRepository userRepository;
    @Mock WalletRepository walletRepository;
    @Mock PasswordEncoder encoder;

    @InjectMocks UpdatePinService service;

    @Test
    void shouldUpdatePinSuccessfullyWhenPasswordIsCorrect() {

        var userId = UUID.randomUUID();
        var currentPassword = "Password123!";
        var newPinRaw = "1264";
        var newPinHash = "encoded_pin_hash";

        var command = new UpdatePinCommand(userId, currentPassword, newPinRaw);

        var user = new User();
        user.setId(userId);
        user.setPassword("stored_password_hash");

        var wallet = new Wallet();
        wallet.setTransactionPin("old_pin_hash");

        BDDMockito.given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(currentPassword, user.getPassword()))
                .willReturn(true);

        BDDMockito.given(walletRepository.findByUser(user))
                .willReturn(Optional.of(wallet));

        BDDMockito.given(encoder.encode(newPinRaw))
                .willReturn(newPinHash);

        service.execute(command);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());

        Wallet savedWallet = walletCaptor.getValue();

        Assertions.assertEquals(newPinHash, savedWallet.getTransactionPin());
        Assertions.assertNotEquals("old_pin_hash", savedWallet.getTransactionPin());
        Assertions.assertNotNull(savedWallet.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        var command = new UpdatePinCommand(UUID.randomUUID(), "WrongPass", "1264");

        var user = new User();
        user.setPassword("real_hash");

        BDDMockito.given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(any(), any()))
                .willReturn(false);

        Assertions.assertThrows(IncorrectCurrentPasswordException.class, () -> {
            service.execute(command);
        });

        verify(walletRepository, never()).findByUser(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldThrowWalletNotFoundExceptionWhenUserExistsButHasNoWallet() {
        var userId = UUID.randomUUID();
        var command = new UpdatePinCommand(userId, "Pass123!", "1264");
        var user = new User();

        BDDMockito.given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(any(), any()))
                .willReturn(true);

        BDDMockito.given(walletRepository.findByUser(user))
                .willReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class, () -> {
            service.execute(command);
        });

        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        var command = new UpdatePinCommand(UUID.randomUUID(), "Pass", "1264");

        BDDMockito.given(userRepository.findById(any()))
                .willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            service.execute(command);
        });

        verify(walletRepository, never()).save(any());
    }
}