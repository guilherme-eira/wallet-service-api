package io.github.guilherme_eira.wallet_service.application.mapper;

import io.github.guilherme_eira.wallet_service.application.dto.output.DepositOutput;
import io.github.guilherme_eira.wallet_service.application.dto.output.TransactionOutput;
import io.github.guilherme_eira.wallet_service.application.dto.output.TransferOutput;
import io.github.guilherme_eira.wallet_service.application.dto.output.WithdrawOutput;
import io.github.guilherme_eira.wallet_service.domain.enumeration.OperationType;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransactionOutputMapper {

    public TransferOutput toTransferOutput(Transaction transaction){
        return new TransferOutput(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getReceiver().getOwner().getName(),
                transaction.getCreatedAt()
        );
    }

    public DepositOutput toDepositOutput(Transaction transaction){
        return new DepositOutput(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCreatedAt()
        );
    }

    public WithdrawOutput toWithdrawOutput(Transaction transaction){
        return new WithdrawOutput(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCreatedAt()
        );
    }

    public Page<TransactionOutput> toTransactionOutput(Page<Transaction> transactions, UUID walletId){
        return transactions.map(t -> {
            return new TransactionOutput(
                    t.getId(),
                    t.getType(),
                    t.getSender() == null || t.getSender().getId() != walletId? OperationType.CREDIT : OperationType.DEBIT,
                    t.getAmount(),
                    t.getCreatedAt(),
                    t.getReceiver() == null? null : t.getReceiver().getOwner().getName()
            );
        });
    }
}
