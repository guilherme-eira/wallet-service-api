package io.github.guilherme_eira.wallet_service.domain.valueobject;

import io.github.guilherme_eira.wallet_service.domain.exception.InvalidTransactionPinException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class TransactionPinTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1592",
            "8042",
            "1357",
            "0246",
            "1212",
            "1235",
            "4320"
    })
    void shouldCreatePin_WhenValid(String validPin) {
        TransactionPin pin = new TransactionPin(validPin);
        Assertions.assertEquals(validPin, pin.getValue());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowException_WhenNullOrBlank(String invalidPin) {
        var ex = Assertions.assertThrows(InvalidTransactionPinException.class, () -> new TransactionPin(invalidPin));
        Assertions.assertEquals("O pin não pode ser nulo ou vazio", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "12345",
            "abcd",
            "12a4",
            "12 4"
    })
    void shouldThrowException_WhenFormatIsInvalid(String invalidPin) {
        var ex = Assertions.assertThrows(InvalidTransactionPinException.class, () -> new TransactionPin(invalidPin));
        Assertions.assertEquals("O pin deve conter exatamente 4 números", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0000",
            "1111",
            "5555",
            "9999"
    })
    void shouldThrowException_WhenAllDigitsAreRepeated(String invalidPin) {
        var ex = Assertions.assertThrows(InvalidTransactionPinException.class, () -> new TransactionPin(invalidPin));
        Assertions.assertEquals("O pin não deve apresentar somente dígitos repetidos", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0123",
            "1234",
            "2345",
            "5678",
            "6789",
            "3210",
            "4321",
            "6543",
            "9876"
    })
    void shouldThrowException_WhenPinIsSequential(String invalidPin) {
        var ex = Assertions.assertThrows(InvalidTransactionPinException.class, () -> new TransactionPin(invalidPin));
        Assertions.assertEquals("O pin não deve ser uma sequência", ex.getMessage());
    }

    @Test
    void shouldRespectEqualityContract() {
        TransactionPin p1 = new TransactionPin("1029");
        TransactionPin p2 = new TransactionPin("1029");
        TransactionPin p3 = new TransactionPin("5821");

        Assertions.assertEquals(p1, p2);
        Assertions.assertEquals(p1.hashCode(), p2.hashCode());
        Assertions.assertNotEquals(p1, p3);
    }
}