package io.github.guilherme_eira.wallet_service.adapter.outbound.auth;

import com.atlassian.onetime.core.TOTPGenerator;
import com.atlassian.onetime.model.TOTPSecret;
import com.atlassian.onetime.service.RandomSecretProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.MfaProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MfaProviderAdapter implements MfaProvider {

    @Override
    public String generateSecret() {
        return new RandomSecretProvider().generateSecret().getBase32Encoded();
    }

    @Override
    public Boolean validateMfaCode(String enteredCode, String userSecret) {
        var decodedSecret = TOTPSecret.Companion.fromBase32EncodedString(userSecret);
        var expectedCode = new TOTPGenerator().generateCurrent(decodedSecret).getValue();
        return enteredCode.equals(expectedCode);
    }
}
