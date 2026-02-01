package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.TransactionOutput;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.GetTransactionsUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.TransactionRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTransactionsService implements GetTransactionsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionOutputMapper outputMapper;

    @Override
    public Page<TransactionOutput> execute(UUID id, Pageable pageable) {
        var wallet = walletRepository.findByUserId(id).orElseThrow(WalletNotFoundException::new);
        var transactions = transactionRepository.findAllByWalletId(wallet.getId(), pageable);
        return outputMapper.toTransactionOutput(transactions, wallet.getId());
    }
}
