package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.WithdrawCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.WithdrawOutput;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectPinException;
import io.github.guilherme_eira.wallet_service.application.exception.TransactionNotAuthorizedException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.*;
import io.github.guilherme_eira.wallet_service.application.validator.WithdrawValidator;
import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceTest {

    @Mock WalletRepository walletRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock UserRepository userRepository; // Nova dependência
    @Mock NotificationGateway notificationGateway;
    @Mock TransactionOutputMapper outputMapper;
    @Mock BankTransferGateway bankTransferGateway;
    @Mock WithdrawValidator validatorMock;

    private WithdrawService service;

    @BeforeEach
    void setUp() {
        service = new WithdrawService(
                walletRepository,
                transactionRepository,
                userRepository, // Injetando
                notificationGateway,
                outputMapper,
                bankTransferGateway,
                List.of(validatorMock)
        );
    }

    @Test
    void shouldWithdrawSuccessfullyWhenBankAuthorizes() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("100.00");
        var pixKey = "test@pix.com";
        var cmd = new WithdrawCommand(userId, amount, "1234", pixKey);

        User user = new User();
        user.setId(userId);
        user.setEmail("user@test.com");

        Wallet wallet = new Wallet();
        wallet.setOwner(user);
        wallet.setBalance(new BigDecimal("200.00"));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        given(walletRepository.findByUserWithLock(user)).willReturn(Optional.of(wallet));

        given(bankTransferGateway.transferFunds(amount, pixKey)).willReturn(true);

        given(outputMapper.toWithdrawOutput(any())).willReturn(new WithdrawOutput(null, null, null));

        service.execute(cmd);

        verify(validatorMock).validate(wallet, amount, "1234");

        Assertions.assertEquals(new BigDecimal("100.00"), wallet.getBalance());
        verify(walletRepository).save(wallet);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        Assertions.assertEquals(TransactionType.WITHDRAW, txCaptor.getValue().getType());

        verify(notificationGateway).sendWithdrawalSuccessEmail("user@test.com", amount);
    }

    @Test
    void shouldThrowExceptionWhenBankRefusesTransfer() {
        var userId = UUID.randomUUID();
        var amount = new BigDecimal("100.00");
        var cmd = new WithdrawCommand(userId, amount,"1234", "key");

        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setOwner(user);
        wallet.setBalance(new BigDecimal("200.00"));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(walletRepository.findByUserWithLock(user)).willReturn(Optional.of(wallet));

        given(bankTransferGateway.transferFunds(any(), any())).willReturn(false);

        Assertions.assertThrows(TransactionNotAuthorizedException.class, () ->
                service.execute(cmd)
        );

        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
        verify(notificationGateway, never()).sendWithdrawalSuccessEmail(any(), any());
    }

    @Test
    void shouldStopProcessWhenValidatorFails() {
        var userId = UUID.randomUUID();
        var cmd = new WithdrawCommand(userId, BigDecimal.TEN, "wrong_pin", "key");

        User user = new User();
        Wallet wallet = new Wallet();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(walletRepository.findByUserWithLock(user)).willReturn(Optional.of(wallet));

        doThrow(new IncorrectPinException()).when(validatorMock).validate(any(), any(), any());

        Assertions.assertThrows(IncorrectPinException.class, () -> service.execute(cmd));

        verify(bankTransferGateway, never()).transferFunds(any(), any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        var cmd = new WithdrawCommand(UUID.randomUUID(), BigDecimal.TEN, "1234", "key");

        given(userRepository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> service.execute(cmd));

        verify(walletRepository, never()).findByUserWithLock(any());
        verify(bankTransferGateway, never()).transferFunds(any(), any());
    }

    @Test
    void shouldThrowWalletNotFoundExceptionWhenWalletDoesNotExist() {
        var userId = UUID.randomUUID();
        var cmd = new WithdrawCommand(userId, BigDecimal.TEN, "1234", "key");
        User user = new User();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(walletRepository.findByUserWithLock(user)).willReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class, () -> service.execute(cmd));

        verify(bankTransferGateway, never()).transferFunds(any(), any());
    }
}