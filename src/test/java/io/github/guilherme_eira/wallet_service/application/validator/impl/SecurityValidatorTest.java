package io.github.guilherme_eira.wallet_service.application.validator.impl;

import io.github.guilherme_eira.wallet_service.application.exception.IncorrectPinException;
import io.github.guilherme_eira.wallet_service.application.exception.TransferBlockedException;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SecurityValidatorTest {

    @Mock PasswordEncoder encoder;
    @Mock WalletRepository walletRepository;
    @InjectMocks SecurityValidator validator;

    @Test
    void shouldSucceedWhenPinIsCorrect() {
        Wallet sender = new Wallet();
        sender.setTransactionPin("encoded_pin");
        sender.setPinAttempts(2);

        given(encoder.matches("1234", "encoded_pin")).willReturn(true);

        validator.validate(sender, null, null, "1234");

        Assertions.assertEquals(0, sender.getPinAttempts());
        verify(walletRepository).save(sender);
    }

    @Test
    void shouldIncrementAttemptsWhenPinIsIncorrect() {
        Wallet sender = new Wallet();
        sender.setTransactionPin("encoded_pin");
        sender.setPinAttempts(0);

        given(encoder.matches("wrong", "encoded_pin")).willReturn(false);

        Assertions.assertThrows(IncorrectPinException.class, () ->
                validator.validate(sender, null, null, "wrong")
        );

        Assertions.assertEquals(1, sender.getPinAttempts());
        verify(walletRepository).save(sender);
    }

    @Test
    void shouldBlockWalletAfterThreeFailedAttempts() {
        Wallet sender = new Wallet();
        sender.setTransactionPin("encoded_pin");
        sender.setPinAttempts(2);

        given(encoder.matches("wrong", "encoded_pin")).willReturn(false);

        Assertions.assertThrows(TransferBlockedException.class, () ->
                validator.validate(sender, null, null, "wrong")
        );

        Assertions.assertEquals(3, sender.getPinAttempts());
        Assertions.assertNotNull(sender.getPinBlockedUntil());
        verify(walletRepository).save(sender);
    }

    @Test
    void shouldThrowExceptionWhenAlreadyBlocked() {
        Wallet sender = new Wallet();
        sender.setPinBlockedUntil(LocalDateTime.now().plusMinutes(10));

        Assertions.assertThrows(TransferBlockedException.class, () ->
                validator.validate(sender, null, null, "any")
        );

        verify(walletRepository, never()).save(any());
    }
}