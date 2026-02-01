package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.TransferCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.TransferOutput;
import io.github.guilherme_eira.wallet_service.application.exception.*;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.TransferUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.*;
import io.github.guilherme_eira.wallet_service.application.validator.TransferValidator;
import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import io.github.guilherme_eira.wallet_service.domain.valueobject.TaxId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionAuthorizer authorizer;
    private final NotificationGateway notificationGateway;
    private final TransactionOutputMapper outputMapper;
    private final List<TransferValidator> validators;

    @Override
    @Transactional(dontRollbackOn = {
            TransferBlockedException.class,
            IncorrectPinException.class,
            LimitExceededException.class
    })
    public TransferOutput execute(TransferCommand cmd) {

        var sender = userRepository.findById(cmd.senderId())
                .orElseThrow(UserNotFoundException::new);

        var receiver = userRepository.findByTaxId(new TaxId(cmd.receiverTaxId()).getValue())
                .orElseThrow(UserNotFoundException::new);

        if (!receiver.isVerified()) throw new ReceiverNotVerifiedException();

        var senderWallet = walletRepository.findByUserWithLock(sender)
                .orElseThrow(WalletNotFoundException::new);

        var receiverWallet = walletRepository.findByUserWithLock(receiver)
                .orElseThrow(WalletNotFoundException::new);

        validators.forEach(validator -> validator.validate(senderWallet, receiverWallet, cmd.amount(),
                cmd.transactionPin()));

        senderWallet.debit(cmd.amount());

        if (!authorizer.authorizeTransaction()) {
            throw new TransactionNotAuthorizedException("Transferência não autorizada");
        }

        receiverWallet.credit(cmd.amount());

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        var transaction = Transaction.create(senderWallet, receiverWallet, cmd.amount(), TransactionType.TRANSFER);
        transactionRepository.save(transaction);

        notificationGateway.sendTransferSentEmail(senderWallet.getOwner().getEmail(), receiverWallet.getOwner().getName(),
                transaction.getAmount());
        notificationGateway.sendTransferReceivedEmail(receiverWallet.getOwner().getEmail(),
                senderWallet.getOwner().getName(),
                transaction.getAmount());

        return outputMapper.toTransferOutput(transaction);
    }
}

