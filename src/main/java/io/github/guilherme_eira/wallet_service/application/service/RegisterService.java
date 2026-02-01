package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.RegisterCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.RegisterOutput;
import io.github.guilherme_eira.wallet_service.application.exception.UserAlreadyExistsException;
import io.github.guilherme_eira.wallet_service.application.mapper.AuthOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.in.RegisterUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.*;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.VerificationToken;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import io.github.guilherme_eira.wallet_service.domain.valueobject.Email;
import io.github.guilherme_eira.wallet_service.domain.valueobject.Password;
import io.github.guilherme_eira.wallet_service.domain.valueobject.TaxId;
import io.github.guilherme_eira.wallet_service.domain.valueobject.TransactionPin;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final NotificationGateway notificationGateway;
    private final PasswordEncoder passwordEncoder;
    private final AuthOutputMapper authMapper;

    @Override
    @Transactional
    public RegisterOutput execute(RegisterCommand cmd) {

        var email = new Email(cmd.email());
        var taxId = new TaxId(cmd.taxId());
        var password = new Password(cmd.password());
        var transactionPin = new TransactionPin(cmd.transactionPin());

        if (userRepository.existsByEmailOrTaxId(email.getValue(), taxId.getValue())){
            throw new UserAlreadyExistsException();
        }

        var newUser = userRepository.save(User.create(
                cmd.name(),
                taxId.getValue(),
                email.getValue(),
                passwordEncoder.encode(password.getValue()),
                cmd.type()
        ));

        walletRepository.save(Wallet.create(
                newUser,
                passwordEncoder.encode(transactionPin.getValue()))
        );

        var verificationToken = VerificationToken.create(newUser);
        verificationTokenRepository.save(verificationToken);

        notificationGateway.sendVerificationEmail(email.getValue(), verificationToken.getToken());

        return authMapper.toRegisterOutput(newUser, true);
    }
}
