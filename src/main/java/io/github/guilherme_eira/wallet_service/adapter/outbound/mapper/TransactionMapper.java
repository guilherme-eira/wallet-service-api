package io.github.guilherme_eira.wallet_service.adapter.outbound.mapper;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.DepositResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.TransactionResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.TransferResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.WithdrawResponse;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.TransactionEntity;
import io.github.guilherme_eira.wallet_service.application.dto.input.WithdrawCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.DepositOutput;
import io.github.guilherme_eira.wallet_service.application.dto.output.TransactionOutput;
import io.github.guilherme_eira.wallet_service.application.dto.output.TransferOutput;
import io.github.guilherme_eira.wallet_service.application.dto.output.WithdrawOutput;
import io.github.guilherme_eira.wallet_service.application.mapper.TransactionOutputMapper;
import io.github.guilherme_eira.wallet_service.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper( componentModel = "spring")
public interface TransactionMapper {
    TransactionEntity toEntity(Transaction transaction);
    Transaction toDomain(TransactionEntity transactionEntity);
    TransferResponse toTransferResponse(TransferOutput output);
    DepositResponse toDepositResponse(DepositOutput output);
    WithdrawResponse toWithdrawResponse(WithdrawOutput output);
    TransactionResponse toTransactionResponse(TransactionOutput output);
}
