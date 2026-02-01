package io.github.guilherme_eira.wallet_service.adapter.outbound.mapper;

import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.PasswordResetTokenEntity;
import io.github.guilherme_eira.wallet_service.domain.model.PasswordResetToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordResetTokenMapper {
    PasswordResetTokenEntity toEntity(PasswordResetToken domain);
    PasswordResetToken toDomain(PasswordResetTokenEntity entity);
}
