package io.github.guilherme_eira.wallet_service.domain.model;

import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.github.guilherme_eira.wallet_service.domain.exception.InsufficientBalanceException;
import io.github.guilherme_eira.wallet_service.domain.exception.InvalidAmountException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class WalletTest {

    @Test
    void shouldCreateWalletWithCorrectLimitsForCommonUser() {
        User common = new User();
        common.setType(UserType.COMMON);

        Wallet wallet = Wallet.create(common, "1234");

        Assertions.assertEquals(new BigDecimal("2000.00"), wallet.getTransactionLimit());
        Assertions.assertEquals(new BigDecimal("5000.00"), wallet.getDailyLimit());
        Assertions.assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void shouldCreateWalletWithCorrectLimitsForMerchant() {
        User merchant = new User();
        merchant.setType(UserType.MERCHANT);

        Wallet wallet = Wallet.create(merchant, "1234");

        Assertions.assertEquals(new BigDecimal("5000.00"), wallet.getTransactionLimit());
        Assertions.assertEquals(new BigDecimal("15000.00"), wallet.getDailyLimit());
    }

    @Test
    void shouldDebitSuccessfullyWhenBalanceIsSufficient() {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("100.00"));

        wallet.debit(new BigDecimal("40.00"));

        Assertions.assertEquals(new BigDecimal("60.00"), wallet.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("100.00"));

        Assertions.assertThrows(InsufficientBalanceException.class, () ->
                wallet.debit(new BigDecimal("100.01"))
        );
    }

    @Test
    void shouldThrowExceptionWhenDebitAmountIsInvalid() {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("100.00"));

        Assertions.assertThrows(InvalidAmountException.class, () -> wallet.debit(BigDecimal.ZERO));
        Assertions.assertThrows(InvalidAmountException.class, () -> wallet.debit(new BigDecimal("-10.00")));
    }

    @Test
    void shouldCreditSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("10.00"));

        wallet.credit(new BigDecimal("20.00"));

        Assertions.assertEquals(new BigDecimal("30.00"), wallet.getBalance());
    }

    @Test
    void shouldIdentifyBlockedPin() {
        Wallet wallet = new Wallet();

        wallet.setPinBlockedUntil(LocalDateTime.now().plusHours(1));
        Assertions.assertTrue(wallet.isPinBlocked());

        wallet.setPinBlockedUntil(LocalDateTime.now().minusHours(1));
        Assertions.assertFalse(wallet.isPinBlocked());

        wallet.setPinBlockedUntil(null);
        Assertions.assertFalse(wallet.isPinBlocked());
    }
}