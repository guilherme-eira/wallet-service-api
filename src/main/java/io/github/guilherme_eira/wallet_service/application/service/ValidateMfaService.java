package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;
import io.github.guilherme_eira.wallet_service.application.dto.input.ValidateMfaCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectMfaCodeException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.in.ValidateMfaUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.MfaProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateMfaService implements ValidateMfaUseCase {

    private final UserRepository repository;
    private final MfaProvider mfaProvider;
    private final TokenProvider tokenProvider;

    @Override
    public AuthOutput execute(ValidateMfaCommand cmd) {
        var user = repository.findById(cmd.id())
                .orElseThrow(UserNotFoundException::new);
        if (!mfaProvider.validateMfaCode(cmd.code(), user.getTwoFactorSecret())){
            throw new IncorrectMfaCodeException();
        }
        var accessToken = tokenProvider.generateAccessToken(user, "ROLE_FULL_ACCESS");
        var refreshToken = tokenProvider.generateRefreshToken(user);
        return new AuthOutput(accessToken, refreshToken, false);
    }
}
