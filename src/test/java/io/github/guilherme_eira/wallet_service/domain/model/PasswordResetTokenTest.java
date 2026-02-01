package io.github.guilherme_eira.wallet_service.domain.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


class PasswordResetTokenTest {

    @Test
    void shouldIdentifyExpiredToken() {
        PasswordResetToken token = new PasswordResetToken();

        token.setTokenExpiration(LocalDateTime.now().plusMinutes(10));
        Assertions.assertFalse(token.isExpired());

        token.setTokenExpiration(LocalDateTime.now().minusSeconds(1));
        Assertions.assertTrue(token.isExpired());
    }

    @Test
    void shouldBlockResendWithinThreeMinutes() {
        PasswordResetToken token = new PasswordResetToken();

        token.setCreatedAt(LocalDateTime.now());
        Assertions.assertTrue(token.isResendBlocked());

        token.setCreatedAt(LocalDateTime.now().minusMinutes(2).minusSeconds(59));
        Assertions.assertTrue(token.isResendBlocked());

        token.setCreatedAt(LocalDateTime.now().minusMinutes(3).minusSeconds(1));
        Assertions.assertFalse(token.isResendBlocked());
    }

}