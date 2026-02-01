package io.github.guilherme_eira.wallet_service.domain.valueobject;

import io.github.guilherme_eira.wallet_service.domain.exception.InvalidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "password123",
            "StrongPass1!",
            "ABCDefgh1",
            "1a2b3c4d"
    })
    void shouldCreatePasswordWhenValid(String rawPassword) {
        Password password = new Password(rawPassword);
        Assertions.assertEquals(rawPassword, password.getValue());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", "   ",
    })
    void shouldThrowExceptionWhenNullOrBlank(String rawPassword) {
        var exception = Assertions.assertThrows(
                InvalidPasswordException.class,
                () -> new Password(rawPassword)
        );
        Assertions.assertEquals("A senha não pode ser nula ou vazia", exception.getMessage());
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "1a2b3c4",
            "abc123",
            "a1",
            "123456a"
    })
    void shouldThrowExceptionWhenTooShort(String rawPassword) {
        var exception = Assertions.assertThrows(
                InvalidPasswordException.class,
                () -> new Password(rawPassword)
        );
        Assertions.assertEquals("A senha deve apresentar pelo menos 8 caracteres", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abcdefgh",
            "ABCDEFGH",
            "12345678",
            "!@#$%^&*",
            "password",
            "1234567890"
    })
    void shouldThrowExceptionWhenComplexityNotMet(String rawPassword) {
        var exception = Assertions.assertThrows(
                InvalidPasswordException.class,
                () -> new Password(rawPassword)
        );
        Assertions.assertEquals("A senha deve apresentar pelo menos uma letra e um número", exception.getMessage());
    }

    @Test
    void shouldRespectEqualityContract() {
        String secret = "Secret123";

        Password p1 = new Password(secret);
        Password p2 = new Password(secret);
        Password p3 = new Password("Other123");

        Assertions.assertEquals(p1, p2);
        Assertions.assertEquals(p1.hashCode(), p2.hashCode());

        Assertions.assertNotEquals(p1, p3);
    }
}