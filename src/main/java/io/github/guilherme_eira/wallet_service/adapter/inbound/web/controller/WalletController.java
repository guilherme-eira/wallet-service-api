package io.github.guilherme_eira.wallet_service.adapter.inbound.web.controller;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.UpdatePinRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.ErrorResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.WalletResponse;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.WalletMapper;
import io.github.guilherme_eira.wallet_service.application.dto.input.UpdatePinCommand;
import io.github.guilherme_eira.wallet_service.application.port.in.GetWalletUseCase;
import io.github.guilherme_eira.wallet_service.application.port.in.UpdatePinUseCase;
import io.github.guilherme_eira.wallet_service.infra.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Tag(name = "Carteira", description = "Consulta de saldo/limites e gestão do PIN de transação")
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final GetWalletUseCase getWalletUseCase;
    private final UpdatePinUseCase updatePinUseCase;
    private final WalletMapper mapper;

    @Operation(summary = "Consultar Carteira", description = "Retorna o saldo atual, limites diários e noturnos da carteira do usuário.")
    @ApiResponse(responseCode = "200", description = "Dados da carteira retornados com sucesso")
    @GetMapping
    public ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal UserPrincipal principal){
        var output = getWalletUseCase.execute(principal.getId());
        var response = mapper.toWalletResponse(output);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Alterar PIN de Transação", description = "Define um novo PIN de 4 dígitos. Requer a senha de login atual para autorização.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "PIN alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Novo PIN inválido (deve ter 4 dígitos)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Senha atual incorreta", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/pin")
    public ResponseEntity<Void> updatePin(@Valid @RequestBody UpdatePinRequest req, @AuthenticationPrincipal UserPrincipal principal){
        var cmd = new UpdatePinCommand(principal.getId(), req.currentPassword(), req.newPin());
        updatePinUseCase.execute(cmd);
        return ResponseEntity.noContent().build();
    }
}