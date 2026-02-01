package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.DepositCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.DepositOutput;
import io.github.guilherme_eira.wallet_service.application.exception.ReceiverNotVerifiedException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotVerifiedException;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.DepositUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.NotificationGateway;
import io.github.guilherme_eira.wallet_service.application.port.out.TransactionRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import io.github.guilherme_eira.wallet_service.domain.valueobject.TaxId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService implements DepositUseCase {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationGateway notificationGateway;
    private final TransactionOutputMapper outputMapper;

    @Override
    @Transactional
    public DepositOutput execute(DepositCommand cmd) {

        var user = userRepository.findByTaxId(new TaxId(cmd.paymentKey()).getValue())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isVerified()) throw new ReceiverNotVerifiedException();

        var wallet = walletRepository.findByUserWithLock(user)
                .orElseThrow(WalletNotFoundException::new);

        wallet.credit(cmd.amount());

        walletRepository.save(wallet);

        var transaction = Transaction.create(null, wallet, cmd.amount(), TransactionType.DEPOSIT);
        transactionRepository.save(transaction);

        notificationGateway.sendDepositReceivedEmail(wallet.getOwner().getEmail(), cmd.amount());

        return outputMapper.toDepositOutput(transaction);
    }
}
