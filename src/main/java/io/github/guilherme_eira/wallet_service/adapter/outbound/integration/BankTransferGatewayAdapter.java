package io.github.guilherme_eira.wallet_service.adapter.outbound.integration;

import io.github.guilherme_eira.wallet_service.adapter.outbound.integration.client.MockIntegrationFeignClient;
import io.github.guilherme_eira.wallet_service.adapter.outbound.integration.client.dto.BankTransferRequest;
import io.github.guilherme_eira.wallet_service.application.port.out.BankTransferGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankTransferGatewayAdapter implements BankTransferGateway {

    private final MockIntegrationFeignClient feignClient;

    @Override
    public boolean transferFunds(BigDecimal amount, String paymentKey) {
        try {
            var request = new BankTransferRequest(amount, paymentKey);
            var response = feignClient.processWithdraw(request);
            return response != null && "CONFIRMED".equalsIgnoreCase(response.status());
        } catch (Exception e) {
            log.warn("Erro ao enviar saque para banco externo: {}", e.getMessage());
            return false;
        }
    }
}
