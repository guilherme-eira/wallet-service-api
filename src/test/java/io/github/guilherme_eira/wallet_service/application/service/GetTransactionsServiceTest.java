package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.TransactionOutput;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.TransactionRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetTransactionsServiceTest {

    @Mock WalletRepository walletRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock TransactionOutputMapper outputMapper;

    @InjectMocks GetTransactionsService service;

    @Test
    void shouldReturnTransactionsWhenWalletExists() {

        var userId = UUID.randomUUID();
        var walletId = UUID.randomUUID();
        var wallet = new Wallet();
        wallet.setId(walletId);

        var pageable = PageRequest.of(0, 10);

        List<Transaction> txList = List.of(new Transaction());
        Page<Transaction> txPage = new PageImpl<>(txList);

        Page<TransactionOutput> outputPage = new PageImpl<>(List.of(new TransactionOutput(null, null, null, null, null, null)));

        given(walletRepository.findByUserId(userId)).willReturn(Optional.of(wallet));
        given(transactionRepository.findAllByWalletId(walletId, pageable)).willReturn(txPage);
        given(outputMapper.toTransactionOutput(txPage, walletId)).willReturn(outputPage);

        var result = service.execute(userId, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());

        verify(transactionRepository).findAllByWalletId(eq(walletId), eq(pageable));
    }

    @Test
    void shouldThrowExceptionWhenWalletNotFound() {
        var userId = UUID.randomUUID();
        var pageable = Pageable.unpaged();

        given(walletRepository.findByUserId(userId)).willReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class, () -> {
            service.execute(userId, pageable);
        });
    }
}