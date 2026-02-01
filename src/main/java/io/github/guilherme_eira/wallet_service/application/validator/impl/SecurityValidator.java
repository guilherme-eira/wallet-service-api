package io.github.guilherme_eira.wallet_service.application.validator.impl;

import io.github.guilherme_eira.wallet_service.application.exception.IncorrectPinException;
import io.github.guilherme_eira.wallet_service.application.exception.TransferBlockedException;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.application.validator.TransferValidator;
import io.github.guilherme_eira.wallet_service.application.validator.WithdrawValidator;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Order(1)
public class SecurityValidator implements TransferValidator, WithdrawValidator{

    private final PasswordEncoder encoder;
    private final WalletRepository walletRepository;

    @Override
    public void validate(Wallet sender, Wallet receiver, BigDecimal amount, String pin) {
        checkSecurity(sender, pin);
    }

    @Override
    public void validate(Wallet sender, BigDecimal amount, String pin) {
        checkSecurity(sender, pin);
    }

    private void checkSecurity(Wallet sender, String pin) {

        if (sender.isPinBlocked()) {
            throw new TransferBlockedException("Carteira bloqueada.");
        }
        if (!encoder.matches(pin, sender.getTransactionPin())) {
            sender.incrementAttempts();
            if (sender.getPinAttempts() >= 3) {
                sender.setPinBlockedUntil(LocalDateTime.now().plusMinutes(30));
            }
            walletRepository.save(sender);

            if (sender.isPinBlocked()) {
                throw new TransferBlockedException("Transferências bloqueadas por múltiplas tentativas.");
            }
            throw new IncorrectPinException();
        }

        sender.resetAttempts();
        sender.setPinBlockedUntil(null);
        walletRepository.save(sender);
    }
}
