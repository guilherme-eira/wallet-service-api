package io.github.guilherme_eira.wallet_service.adapter.outbound.mapper;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.WalletResponse;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.WalletEntity;
import io.github.guilherme_eira.wallet_service.application.dto.output.WalletOutput;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletEntity toEntity(Wallet domain);
    Wallet toDomain(WalletEntity entity);
    WalletResponse toWalletResponse(WalletOutput output);
}
