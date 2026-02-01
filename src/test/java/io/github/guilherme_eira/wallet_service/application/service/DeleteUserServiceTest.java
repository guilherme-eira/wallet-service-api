package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.exception.NonZeroBalanceException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    WalletRepository walletRepository;

    @InjectMocks
    DeleteUserService deleteUserService;

    @Test
    void shouldSoftDeleteUserSuccessfullyWhenBalanceIsZero() {
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        user.setActive(true);

        var wallet = new Wallet();
        wallet.setOwner(user);
        wallet.setBalance(BigDecimal.ZERO);

        BDDMockito.given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(walletRepository.findByUser(user))
                .willReturn(Optional.of(wallet));

        deleteUserService.execute(userId);

        verify(userRepository).save(user);

        Assertions.assertFalse(user.isActive(), "O usuário deveria estar inativo");
        Assertions.assertNotNull(user.getDeletedAt(), "A data de exclusão não deveria ser nula");
    }

    @Test
    void shouldThrowExceptionIfBalanceIsNotZero(){
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        var wallet = new Wallet();
        wallet.setOwner(user);
        wallet.setBalance(new BigDecimal(100));

        BDDMockito.given(userRepository.findById(userId)).willReturn(Optional.of(user));
        BDDMockito.given(walletRepository.findByUser(user)).willReturn(Optional.of(wallet));

        Assertions.assertThrows(NonZeroBalanceException.class, () -> {
            deleteUserService.execute(userId);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        var userId = UUID.randomUUID();

        BDDMockito.given(userRepository.findById(userId))
                .willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            deleteUserService.execute(userId);
        });

        verify(walletRepository, never()).findByUser(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenWalletDoesNotExist() {
        var userId = UUID.randomUUID();
        var user = new User();

        BDDMockito.given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(walletRepository.findByUser(user))
                .willReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class, () -> {
            deleteUserService.execute(userId);
        });

        verify(userRepository, never()).save(any());
    }

}