package io.github.guilherme_eira.wallet_service.adapter.outbound.integration.client.dto;

public record AuthorizerResponse(
        String status,
        Data data
) {
    public record Data(
            boolean authorization
    ) {}
}
