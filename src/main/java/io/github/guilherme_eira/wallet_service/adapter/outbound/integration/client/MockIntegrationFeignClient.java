package io.github.guilherme_eira.wallet_service.adapter.outbound.integration.client;

import io.github.guilherme_eira.wallet_service.adapter.outbound.integration.client.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mock-client", url = "${client.mock.url}")
public interface MockIntegrationFeignClient {

    @GetMapping("/api/v2/authorize")
    AuthorizerResponse authorize();

    @PostMapping("/api/bank/transfer")
    BankTransferResponse processWithdraw(@RequestBody BankTransferRequest request);
}
