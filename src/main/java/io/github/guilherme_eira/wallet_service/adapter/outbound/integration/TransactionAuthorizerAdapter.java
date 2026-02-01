package io.github.guilherme_eira.wallet_service.adapter.outbound.integration;

import io.github.guilherme_eira.wallet_service.adapter.outbound.integration.client.MockIntegrationFeignClient;
import io.github.guilherme_eira.wallet_service.application.port.out.TransactionAuthorizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionAuthorizerAdapter implements TransactionAuthorizer {

    private final MockIntegrationFeignClient feignClient;

    @Override
    public Boolean authorizeTransaction() {
        try {
            var response = feignClient.authorize();
            return response != null
                    && "success".equalsIgnoreCase(response.status())
                    && response.data() != null
                    && response.data().authorization();
        } catch (Exception e) {
            log.error("Erro na comunicação com serviço autorizador", e);
            return false;
        }
    }
}
