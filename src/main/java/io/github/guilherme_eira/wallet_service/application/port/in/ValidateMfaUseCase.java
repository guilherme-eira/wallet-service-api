package io.github.guilherme_eira.wallet_service.application.port.in;

import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;
import io.github.guilherme_eira.wallet_service.application.dto.input.ValidateMfaCommand;

public interface ValidateMfaUseCase {
    AuthOutput execute(ValidateMfaCommand cmd);
}
