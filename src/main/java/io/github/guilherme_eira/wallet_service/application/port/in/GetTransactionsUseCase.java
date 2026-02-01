package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.output.TransactionOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetTransactionsUseCase {
    Page<TransactionOutput> execute(UUID id, Pageable pageable);
}
