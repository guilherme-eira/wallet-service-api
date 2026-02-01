package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.DepositCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.DepositOutput;
import io.github.guilherme_eira.wallet_service.application.exception.ReceiverNotVerifiedException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotVerifiedException;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.NotificationGateway;
import io.github.guilherme_eira.wallet_service.application.port.out.TransactionRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock WalletRepository walletRepository;
    @Mock UserRepository userRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock NotificationGateway notificationGateway;
    @Mock TransactionOutputMapper outputMapper;

    @InjectMocks DepositService depositService;

    @Test
    void shouldDepositSuccessfully() {

        var validCpf = "98028522017";
        var amount = new BigDecimal("100.00");
        var cmd = new DepositCommand(amount, validCpf);

        var user = new User();
        user.setEmail("user@test.com");
        user.setVerified(true);

        Wallet wallet = new Wallet();
        wallet.setOwner(user);
        wallet.setBalance(BigDecimal.ZERO);

        given(userRepository.findByTaxId(any())).willReturn(Optional.of(user));

        given(walletRepository.findByUserWithLock(user)).willReturn(Optional.of(wallet));

        given(outputMapper.toDepositOutput(any()))
                .willReturn(new DepositOutput(null, null, null));

        depositService.execute(cmd);

        Assertions.assertEquals(amount, wallet.getBalance());
        verify(walletRepository).save(wallet);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction savedTransaction = captor.getValue();
        Assertions.assertEquals(TransactionType.DEPOSIT, savedTransaction.getType());
        Assertions.assertEquals(amount, savedTransaction.getAmount());

        verify(notificationGateway).sendDepositReceivedEmail("user@test.com", amount);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        var cmd = new DepositCommand(BigDecimal.TEN, "98028522017");

        given(userRepository.findByTaxId(any())).willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> depositService.execute(cmd));

        verify(walletRepository, never()).findByUserWithLock(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotVerified() {
        var cmd = new DepositCommand(BigDecimal.TEN, "98028522017");

        var user = new User();
        user.setVerified(false);

        given(userRepository.findByTaxId(any())).willReturn(Optional.of(user));

        Assertions.assertThrows(ReceiverNotVerifiedException.class, () -> depositService.execute(cmd));

        verify(walletRepository, never()).findByUserWithLock(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenWalletDoesNotExist() {
        var cmd = new DepositCommand(BigDecimal.TEN, "98028522017");

        var user = new User();
        user.setVerified(true);

        given(userRepository.findByTaxId(any())).willReturn(Optional.of(user));
        given(walletRepository.findByUserWithLock(user)).willReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class, () -> depositService.execute(cmd));

        verify(walletRepository, never()).save(any());
        verify(notificationGateway, never()).sendDepositReceivedEmail(any(), any());
    }
}