package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.TransferCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.TransferOutput;
import io.github.guilherme_eira.wallet_service.application.exception.*;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.*;
import io.github.guilherme_eira.wallet_service.application.validator.TransferValidator;
import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class TransferServiceTest {

    @Mock WalletRepository walletRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock UserRepository userRepository;
    @Mock TransactionAuthorizer authorizer;
    @Mock NotificationGateway notificationGateway;
    @Mock TransactionOutputMapper outputMapper;
    @Mock TransferValidator validatorMock;

    private TransferService service;

    @BeforeEach
    void setUp() {
        service = new TransferService(
                walletRepository,
                transactionRepository,
                userRepository,
                authorizer,
                notificationGateway,
                outputMapper,
                List.of(validatorMock)
        );
    }

    @Test
    void shouldTransferSuccessfullyWhenAllValidationsPass() {

        var senderId = UUID.randomUUID();
        var receiverTaxId = "98028522017";
        var cmd = new TransferCommand(senderId, receiverTaxId, new BigDecimal("100.00"), "1246");

        User senderUser = createUser(senderId, "sender@test.com", true);
        User receiverUser = createUser(UUID.randomUUID(), "receiver@test.com", true);

        Wallet senderWallet = createWallet(senderUser, new BigDecimal("200.00"));
        Wallet receiverWallet = createWallet(receiverUser, new BigDecimal("0.00"));

        given(userRepository.findById(senderId)).willReturn(Optional.of(senderUser));
        given(userRepository.findByTaxId(receiverTaxId)).willReturn(Optional.of(receiverUser));

        given(walletRepository.findByUserWithLock(senderUser)).willReturn(Optional.of(senderWallet));
        given(walletRepository.findByUserWithLock(receiverUser)).willReturn(Optional.of(receiverWallet));

        given(authorizer.authorizeTransaction()).willReturn(true);
        given(outputMapper.toTransferOutput(any())).willReturn(new TransferOutput(null, null, null, null));

        service.execute(cmd);

        verify(validatorMock).validate(senderWallet, receiverWallet, cmd.amount(), cmd.transactionPin());

        Assertions.assertEquals(new BigDecimal("100.00"), senderWallet.getBalance());
        Assertions.assertEquals(new BigDecimal("100.00"), receiverWallet.getBalance());

        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(receiverWallet);

        verify(transactionRepository).save(any(Transaction.class));

        verify(notificationGateway).sendTransferSentEmail(eq("sender@test.com"), any(), any());
        verify(notificationGateway).sendTransferReceivedEmail(eq("receiver@test.com"), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenReceiverIsNotVerified() {
        var cmd = new TransferCommand(UUID.randomUUID(), "98028522017", BigDecimal.TEN, "1246");

        User senderUser = createUser(cmd.senderId(), "s@test.com", true);

        User receiverUser = createUser(UUID.randomUUID(), "r@test.com", false);

        given(userRepository.findById(cmd.senderId())).willReturn(Optional.of(senderUser));
        given(userRepository.findByTaxId(cmd.receiverTaxId())).willReturn(Optional.of(receiverUser));

        Assertions.assertThrows(ReceiverNotVerifiedException.class, () -> service.execute(cmd));

        verify(walletRepository, never()).findByUserWithLock(any());
        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldStopTransferWhenValidatorFails() {
        var cmd = new TransferCommand(UUID.randomUUID(), "98028522017", BigDecimal.TEN, "1246");

        User senderUser = createUser(cmd.senderId(), "s@test.com", true);
        User receiverUser = createUser(UUID.randomUUID(), "r@test.com", true);
        Wallet senderWallet = createWallet(senderUser, new BigDecimal("200.00"));
        Wallet receiverWallet = createWallet(receiverUser, BigDecimal.ZERO);

        given(userRepository.findById(cmd.senderId())).willReturn(Optional.of(senderUser));
        given(userRepository.findByTaxId(cmd.receiverTaxId())).willReturn(Optional.of(receiverUser));
        given(walletRepository.findByUserWithLock(senderUser)).willReturn(Optional.of(senderWallet));
        given(walletRepository.findByUserWithLock(receiverUser)).willReturn(Optional.of(receiverWallet));

        doThrow(new TransferBlockedException("Bloqueado"))
                .when(validatorMock).validate(any(), any(), any(), any());

        Assertions.assertThrows(TransferBlockedException.class, () -> service.execute(cmd));

        Assertions.assertEquals(new BigDecimal("200.00"), senderWallet.getBalance());
        Assertions.assertEquals(BigDecimal.ZERO, receiverWallet.getBalance());

        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAuthorizerRejects() {
        var cmd = new TransferCommand(UUID.randomUUID(), "98028522017", BigDecimal.TEN, "1246");

        User senderUser = createUser(cmd.senderId(), "s@test.com", true);
        User receiverUser = createUser(UUID.randomUUID(), "r@test.com", true);
        Wallet senderWallet = createWallet(senderUser, new BigDecimal("200.00"));
        Wallet receiverWallet = createWallet(receiverUser, BigDecimal.ZERO);

        given(userRepository.findById(cmd.senderId())).willReturn(Optional.of(senderUser));
        given(userRepository.findByTaxId(cmd.receiverTaxId())).willReturn(Optional.of(receiverUser));
        given(walletRepository.findByUserWithLock(senderUser)).willReturn(Optional.of(senderWallet));
        given(walletRepository.findByUserWithLock(receiverUser)).willReturn(Optional.of(receiverWallet));

        given(authorizer.authorizeTransaction()).willReturn(false);

        Assertions.assertThrows(TransactionNotAuthorizedException.class, () -> service.execute(cmd));

        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSenderNotFound() {
        var cmd = new TransferCommand(UUID.randomUUID(), "123", BigDecimal.TEN, "1246");
        given(userRepository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> service.execute(cmd));
    }

    private User createUser(UUID id, String email, boolean verified) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName("Test User");
        user.setType(UserType.COMMON);
        user.setVerified(verified);
        return user;
    }

    private Wallet createWallet(User owner, BigDecimal balance) {
        Wallet w = new Wallet();
        w.setId(UUID.randomUUID());
        w.setOwner(owner);
        w.setBalance(balance);
        return w;
    }
}