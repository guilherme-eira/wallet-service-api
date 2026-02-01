package io.github.guilherme_eira.wallet_service.adapter.outbound.mapper;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.RegisterResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.RegisterRequest;
import io.github.guilherme_eira.wallet_service.application.dto.input.RegisterCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.RegisterOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    RegisterCommand toRegisterCommand(RegisterRequest request);
    RegisterResponse toRegisterResponse(RegisterOutput output);
}
