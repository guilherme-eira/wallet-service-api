package io.github.guilherme_eira.wallet_service.application.mapper;

import io.github.guilherme_eira.wallet_service.application.dto.output.UserOutput;
import io.github.guilherme_eira.wallet_service.application.dto.output.UpdateUserOutput;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class UserOutputMapper {

    public UpdateUserOutput toUpdateUserOutput(User user){
        return new UpdateUserOutput(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUpdatedAt()
        );
    }

    public UserOutput toUserOutput(User user){
        return new UserOutput(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getTaxId(),
                user.getType(),
                user.isTwoFactorActive()
        );
    }
}
