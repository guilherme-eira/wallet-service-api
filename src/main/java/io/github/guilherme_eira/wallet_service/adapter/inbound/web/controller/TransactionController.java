package io.github.guilherme_eira.wallet_service.adapter.inbound.web.controller;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.TransferRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.WithdrawRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.ErrorResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.TransactionResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.TransferResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.WithdrawResponse;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.TransactionMapper;
import io.github.guilherme_eira.wallet_service.application.dto.input.TransferCommand;
import io.github.guilherme_eira.wallet_service.application.dto.input.WithdrawCommand;
import io.github.guilherme_eira.wallet_service.application.port.in.GetTransactionsUseCase;
import io.github.guilherme_eira.wallet_service.application.port.in.TransferUseCase;
import io.github.guilherme_eira.wallet_service.application.port.in.WithdrawUseCase;
import io.github.guilherme_eira.wallet_service.infra.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Operações financeiras (Transferência, Saque e Extrato)")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransferUseCase transferUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final GetTransactionsUseCase getTransactionsUseCase;
    private final TransactionMapper mapper;

    @Operation(summary = "Histórico de Transações", description = "Retorna o extrato do usuário paginado.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal UserPrincipal principal,

            @Parameter(description = "Número da página (0..N)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo para ordenação", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,

            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sort);
        var output = getTransactionsUseCase.execute(principal.getId(), pageable);
        var response = output.map(mapper::toTransactionResponse);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Realizar Transferência", description = "Transfere valores entre carteiras internas. Requer PIN de transação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Destinatário (Receiver) não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Erro de Negócio: Saldo insuficiente, PIN incorreto, Limite diário excedido ou Auto-transferência", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest req,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var cmd = new TransferCommand(principal.getId(), req.receiver(), req.amount(), req.transactionPin());
        var output = transferUseCase.execute(cmd);
        var response = mapper.toTransferResponse(output);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Realizar Saque", description = "Simula um saque externo via chave de pagamento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Erro de Negócio: Saldo insuficiente, PIN incorreto ou Limite excedido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
            @Valid @RequestBody WithdrawRequest req,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var cmd = new WithdrawCommand(principal.getId(), req.amount(), req.transactionPin(), req.paymentKey());
        var output = withdrawUseCase.execute(cmd);
        var response = mapper.toWithdrawResponse(output);
        return ResponseEntity.ok().body(response);
    }
}