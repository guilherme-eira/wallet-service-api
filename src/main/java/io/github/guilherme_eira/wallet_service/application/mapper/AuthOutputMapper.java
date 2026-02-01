package io.github.guilherme_eira.wallet_service.application.mapper;

import io.github.guilherme_eira.wallet_service.application.dto.output.RegisterOutput;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthOutputMapper {

    public RegisterOutput toRegisterOutput(User user, Boolean requiresVerification){
        return new RegisterOutput(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                requiresVerification
        );
    }
}
