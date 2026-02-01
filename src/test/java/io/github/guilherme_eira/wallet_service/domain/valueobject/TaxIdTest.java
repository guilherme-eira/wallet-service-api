package io.github.guilherme_eira.wallet_service.domain.valueobject;

import io.github.guilherme_eira.wallet_service.domain.exception.InvalidTaxIdException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class TaxIdTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "98028522017",
            "093.293.580-04",
            "14010521007",
            "150.583.880-06"
    })
    void shouldCreateCpfWhenValid(String input) {
        TaxId taxId = new TaxId(input);

        String expectedClean = input.replaceAll("\\D", "");
        Assertions.assertEquals(expectedClean, taxId.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "33.000.167/0001-01",
            "00000000000191",
            "60.701.190/0001-04"
    })
    void shouldCreateCnpjWhenValid(String input) {
        TaxId taxId = new TaxId(input);

        String expectedClean = input.replaceAll("\\D", "");
        Assertions.assertEquals(expectedClean, taxId.getValue());
    }


    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowExceptionWhenNullOrBlank(String input) {
        var ex = Assertions.assertThrows(InvalidTaxIdException.class, () -> new TaxId(input));
        Assertions.assertEquals("CPF/CNPJ não pode ser nulo ou vazio", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "1234567890",
            "123456789012",
            "123456789012345"
    })
    void shouldThrowExceptionWhenLengthIsInvalid(String input) {
        var ex = Assertions.assertThrows(InvalidTaxIdException.class, () -> new TaxId(input));
        Assertions.assertEquals("Tamanho inválido", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "000.000.000-00",
            "11111111111",
            "999.999.999-99",
            "22.222.222/2222-22"
    })
    void shouldThrowExceptionWhenAllDigitsAreRepeated(String input) {
        var ex = Assertions.assertThrows(InvalidTaxIdException.class, () -> new TaxId(input));
        Assertions.assertEquals("CPF/CNPJ não deve apresentar somente dígitos repetidos", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123.456.789-00",
            "866.526.490-00",
            "33.000.167/0001-00"
    })
    void shouldThrowExceptionWhenMathematicallyInvalid(String input) {
        var ex = Assertions.assertThrows(InvalidTaxIdException.class, () -> new TaxId(input));
        Assertions.assertEquals("CPF/CNPJ não cumpre a validação matemática", ex.getMessage());
    }

    @Test
    void shouldConsiderFormattedAndCleanValuesEqual() {
        TaxId t1 = new TaxId("999.142.400-89");
        TaxId t2 = new TaxId("99914240089");

        Assertions.assertEquals(t1, t2);
        Assertions.assertEquals(t1.hashCode(), t2.hashCode());
    }
}