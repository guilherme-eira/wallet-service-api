package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.LoginCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.AuthOutput;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCredentialsException;
import io.github.guilherme_eira.wallet_service.application.exception.LoginBlockedException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotVerifiedException;
import io.github.guilherme_eira.wallet_service.application.port.in.LoginUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Transactional(noRollbackFor = {
            IncorrectCredentialsException.class
    })
    public AuthOutput execute(LoginCommand cmd) {

        var user = userRepository.findByEmail(cmd.email())
                .orElseThrow(IncorrectCredentialsException::new);

        if (user.isLoginBlocked()) {
            throw new LoginBlockedException(
                    "Conta bloqueada. Tente novamente após " + user.getLoginBlockedUntil()
            );
        }

        if (!user.isVerified()) {
            throw new UserNotVerifiedException();
        }

        if (!passwordEncoder.matches(cmd.password(), user.getPassword())) {
            user.incrementAttempts();

            if (user.getLoginAttempts() >= 3) {
                user.setLoginBlockedUntil(LocalDateTime.now().plusMinutes(30));
            }

            userRepository.save(user);

            throw new IncorrectCredentialsException();
        }

        if (user.getLoginAttempts() > 0) {
            user.resetAttempts();
            userRepository.save(user);
        }

        if (user.isTwoFactorActive()) {
            String accessToken = tokenProvider.generateAccessToken(user, "ROLE_PRE_AUTH");
            return new AuthOutput(accessToken, null, true);
        }

        var accessToken = tokenProvider.generateAccessToken(user, "ROLE_FULL_ACCESS");
        var refreshToken = tokenProvider.generateRefreshToken(user);

        return new AuthOutput(accessToken, refreshToken, false);
    }
}