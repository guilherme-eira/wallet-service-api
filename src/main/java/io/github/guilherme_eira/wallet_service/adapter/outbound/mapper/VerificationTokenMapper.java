package io.github.guilherme_eira.wallet_service.adapter.outbound.mapper;

import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.VerificationTokenEntity;
import io.github.guilherme_eira.wallet_service.domain.model.VerificationToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VerificationTokenMapper {
    VerificationTokenEntity toEntity(VerificationToken domain);
    VerificationToken toDomain(VerificationTokenEntity entity);
}
