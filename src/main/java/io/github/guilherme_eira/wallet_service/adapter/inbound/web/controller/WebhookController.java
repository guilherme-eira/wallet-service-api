package io.github.guilherme_eira.wallet_service.adapter.inbound.web.controller;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.DepositRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.DepositResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.ErrorResponse;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.TransactionMapper;
import io.github.guilherme_eira.wallet_service.application.dto.input.DepositCommand;
import io.github.guilherme_eira.wallet_service.application.port.in.DepositUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Endpoints de callback para integração com sistemas de pagamento externos")
public class WebhookController {

    private final DepositUseCase depositUseCase;
    private final TransactionMapper mapper;

    @Value("${api.security.webhook-secret}")
    private String webhookSecret;

    @Operation(
            summary = "Receber Depósito (Callback)",
            description = "Endpoint chamado por sistemas externos para confirmar um depósito. Identifica a carteira através da 'paymentKey' (ex: Chave Pix)."
    )
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito processado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Secret do Webhook inválido ou ausente", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Carteira não encontrada para a chave de pagamento informada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/deposits")
    public ResponseEntity<DepositResponse> deposit(
            @Valid @RequestBody DepositRequest req,

            @Parameter(
                    in = ParameterIn.HEADER,
                    name = "X-WEBHOOK-SECRET",
                    description = "Chave secreta compartilhada para validar a origem da requisição",
                    required = true,
                    example = "my-super-secret-key-123"
            )
            @RequestHeader(value = "X-WEBHOOK-SECRET", required = false) String secret
    ) {
        if (secret == null || !secret.equals(webhookSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var cmd = new DepositCommand(req.amount(), req.paymentKey());
        var output = depositUseCase.execute(cmd);

        return ResponseEntity.ok().body(mapper.toDepositResponse(output));
    }
}