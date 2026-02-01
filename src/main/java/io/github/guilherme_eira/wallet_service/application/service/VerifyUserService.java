package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.RegisterOutput;
import io.github.guilherme_eira.wallet_service.application.exception.ExpiredVerificationTokenException;
import io.github.guilherme_eira.wallet_service.application.exception.VerificationTokenNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.AuthOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.VerifyUserUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerifyUserService implements VerifyUserUseCase {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final AuthOutputMapper authMapper;

    @Override
    @Transactional
    public RegisterOutput execute(String token) {
        var verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(VerificationTokenNotFoundException::new);

        if (verificationToken.isExpired()){
            throw new ExpiredVerificationTokenException();
        }

        var user = verificationToken.getUser();
        user.setVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        var verifiedUser = userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return authMapper.toRegisterOutput(verifiedUser, false);
    }
}
