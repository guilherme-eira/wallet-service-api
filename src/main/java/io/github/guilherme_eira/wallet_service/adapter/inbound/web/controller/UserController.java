package io.github.guilherme_eira.wallet_service.adapter.inbound.web.controller;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.MfaSetupRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.UpdatePasswordRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.UpdateUserRequest;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.EnableMfaResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.ErrorResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.UpdateUserResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.UserResponse;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.UserMapper;
import io.github.guilherme_eira.wallet_service.application.dto.input.MfaSetupCommand;
import io.github.guilherme_eira.wallet_service.application.dto.input.UpdatePasswordCommand;
import io.github.guilherme_eira.wallet_service.application.port.in.*;
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
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Gerenciamento de Conta", description = "Endpoints para perfil, segurança (Senha/MFA) e encerramento de conta")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserMapper mapper;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final EnableMfaUseCase enableMfaUseCase;
    private final DisableMfaUseCase disableMfaUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final GetUserUseCase getUserUseCase;

    @Operation(summary = "Obter dados do perfil", description = "Retorna as informações cadastrais do usuário logado.")
    @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso")
    @GetMapping
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserPrincipal principal){
        var output = getUserUseCase.execute(principal.getId());
        var response = mapper.toUserInfoResponse(output);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Atualizar perfil", description = "Permite alterar dados cadastrais básicos (Nome, etc).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<UpdateUserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request, @AuthenticationPrincipal UserPrincipal principal){
        var update = mapper.toUpdateCommand(request, principal.getId());
        var updatedUser = mapper.toUpdateResponse(updateUserUseCase.execute(update));
        return ResponseEntity.ok().body(updatedUser);
    }

    @Operation(summary = "Excluir conta", description = "Encerra definitivamente a conta do usuário. Requer que o saldo esteja zerado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso"),
            @ApiResponse(responseCode = "422", description = "Erro de Negócio: Saldo não é zero", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserPrincipal principal){
        deleteUserUseCase.execute(principal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ativar MFA (2FA)", description = "Habilita a Autenticação de Dois Fatores. Retorna a URL (otpauth) para geração do QR Code.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MFA habilitado, URL do QR Code gerada"),
            @ApiResponse(responseCode = "422", description = "Senha atual incorreta ou MFA já ativo", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/enable-mfa")
    public ResponseEntity<EnableMfaResponse> enableMfa(@Valid @RequestBody MfaSetupRequest req, @AuthenticationPrincipal UserPrincipal principal){
        var cmd = new MfaSetupCommand(principal.getId(), req.currentPassword());
        var url = enableMfaUseCase.execute(cmd);
        var response =  new EnableMfaResponse(url);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Desativar MFA", description = "Remove a proteção de dois fatores da conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "MFA desativado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Senha atual incorreta ou MFA já inativo", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/disable-mfa")
    public ResponseEntity<Void> disableMfa(@Valid @RequestBody MfaSetupRequest req, @AuthenticationPrincipal UserPrincipal principal){
        var cmd = new MfaSetupCommand(principal.getId(), req.currentPassword());
        disableMfaUseCase.execute(cmd);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Alterar senha", description = "Atualiza a senha de acesso. Requer a senha atual para confirmação.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nova senha fraca", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Senha atual incorreta", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest req, @AuthenticationPrincipal UserPrincipal principal){
        var cmd = new UpdatePasswordCommand(principal.getId(), req.currentPassword(), req.newPassword());
        updatePasswordUseCase.execute(cmd);
        return ResponseEntity.noContent().build();
    }
}