package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.WithdrawCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.WithdrawOutput;
import io.github.guilherme_eira.wallet_service.application.exception.TransactionNotAuthorizedException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.WithdrawUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.*;
import io.github.guilherme_eira.wallet_service.application.validator.WithdrawValidator;
import io.github.guilherme_eira.wallet_service.domain.enumeration.TransactionType;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawService implements WithdrawUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationGateway notificationGateway;
    private final TransactionOutputMapper outputMapper;
    private final BankTransferGateway bankTransferGateway;
    private final List<WithdrawValidator> validators;

    @Override
    @Transactional
    public WithdrawOutput execute(WithdrawCommand cmd) {

        var user = userRepository.findById(cmd.id())
                .orElseThrow(UserNotFoundException::new);

        var wallet = walletRepository.findByUserWithLock(user)
                .orElseThrow(WalletNotFoundException::new);

        validators.forEach(validator -> validator.validate(wallet, cmd.amount(), cmd.transactionPin()));

        wallet.debit(cmd.amount());

        boolean transferSuccess = bankTransferGateway.transferFunds(cmd.amount(), cmd.pixKey());

        if (!transferSuccess) {
            throw new TransactionNotAuthorizedException("Transferência recusada pelo banco parceiro");
        }

        walletRepository.save(wallet);

        var transaction = Transaction.create(wallet, null, cmd.amount(), TransactionType.WITHDRAW);
        transactionRepository.save(transaction);

        notificationGateway.sendWithdrawalSuccessEmail(
                wallet.getOwner().getEmail(),
                cmd.amount()
        );

        return outputMapper.toWithdrawOutput(transaction);
    }
}
