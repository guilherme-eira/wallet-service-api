package io.github.guilherme_eira.wallet_service.domain.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class UserTest {

    @Test
    void shouldBlockLoginOnlyWhenDateIsInFuture() {
        User user = new User();

        user.setLoginBlockedUntil(LocalDateTime.now().plusMinutes(10));
        Assertions.assertTrue(user.isLoginBlocked());

        user.setLoginBlockedUntil(LocalDateTime.now().minusMinutes(1));
        Assertions.assertFalse(user.isLoginBlocked());

        user.setLoginBlockedUntil(null);
        Assertions.assertFalse(user.isLoginBlocked());
    }

    @Test
    void shouldIncrementAndResetAttempts() {
        User user = new User();
        user.setLoginAttempts(0);

        user.incrementAttempts();
        user.incrementAttempts();
        Assertions.assertEquals(2, user.getLoginAttempts());

        user.resetAttempts();
        Assertions.assertEquals(0, user.getLoginAttempts());
    }
}