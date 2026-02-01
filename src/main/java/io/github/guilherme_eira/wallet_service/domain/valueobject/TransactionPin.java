package io.github.guilherme_eira.wallet_service.domain.valueobject;

import io.github.guilherme_eira.wallet_service.domain.exception.InvalidTransactionPinException;

import java.util.Objects;

public class TransactionPin {
    private final String pin;

    public TransactionPin(String pin) {
        if( pin == null || pin.isBlank()){
            throw new InvalidTransactionPinException("O pin não pode ser nulo ou vazio");
        }

        if (!pin.matches("\\d{4}")) {
            throw new InvalidTransactionPinException("O pin deve conter exatamente 4 números");
        }

        if (pin.matches("^(\\d)\\1{3}$")){
            throw new InvalidTransactionPinException("O pin não deve apresentar somente dígitos repetidos");
        }

        if (isSequence(pin)) {
            throw new InvalidTransactionPinException("O pin não deve ser uma sequência");
        }

        this.pin = pin;
    }

    public String getValue() {
        return pin;
    }

    private boolean isSequence(String pin){
        var direct = true;
        var reverse = true;
        for (int i = 0; i < 3; i++) {
            var digit = Character.getNumericValue(pin.charAt(i));
            var nextDigit = Character.getNumericValue(pin.charAt(i + 1));
            if (nextDigit != digit + 1){
                direct = false;
            }
            if (nextDigit != digit - 1){
                reverse = false;
            }
        }
        return direct || reverse;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        TransactionPin that = (TransactionPin) object;
        return Objects.equals(pin, that.pin);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pin);
    }
}
