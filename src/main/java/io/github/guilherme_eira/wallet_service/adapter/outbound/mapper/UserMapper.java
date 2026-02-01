package io.github.guilherme_eira.wallet_service.adapter.outbound.mapper;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.UpdateUserRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.UserResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.UpdateUserResponse;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.UserEntity;
import io.github.guilherme_eira.wallet_service.application.dto.input.UpdateUserCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.UserOutput;
import io.github.guilherme_eira.wallet_service.application.dto.output.UpdateUserOutput;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(User domain);
    User toDomain(UserEntity entity);
    @Mapping(source = "id", target = "id")
    UpdateUserCommand toUpdateCommand(UpdateUserRequest request, UUID id);
    UpdateUserResponse toUpdateResponse(UpdateUserOutput output);
    UserResponse toUserInfoResponse(UserOutput output);
}
