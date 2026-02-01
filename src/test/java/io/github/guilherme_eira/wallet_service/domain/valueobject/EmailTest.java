package io.github.guilherme_eira.wallet_service.domain.valueobject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @Test
    void shouldCreateEmailWhenAddressIsValid() {
        String validAddress = "user@test.com";
        Email email = new Email(validAddress);

        Assertions.assertEquals(validAddress, email.getValue());
        Assertions.assertEquals(validAddress, email.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "john.doe@example.com",
            "jane-doe@example.co.uk",
            "user123@provider.net",
            "a@b.com",
            "contact@site.org"
    })
    void shouldAcceptValidFormats(String address) {
        Assertions.assertDoesNotThrow(() -> new Email(address));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", "   ",
    })
    void shouldThrowException_WhenAddressIsBlankOrNull(String address) {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Email(address)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plainaddress",
            "@example.com",
            "user@",
            "user@.com",
            "user@com",
            "user@domain.c",
            "user@domain.cloud",
            "user name@test.com"
    })
    void shouldThrowExceptionWhenFormatIsInvalid(String invalidAddress) {Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Email(invalidAddress)
        );
    }

    @Test
    void shouldRespectValueObjectEquality() {
        Email email1 = new Email("john@test.com");
        Email email2 = new Email("john@test.com");
        Email email3 = new Email("other@test.com");

        Assertions.assertEquals(email1, email2);
        Assertions.assertEquals(email1.hashCode(), email2.hashCode());
        Assertions.assertNotEquals(email1, email3);
    }
}