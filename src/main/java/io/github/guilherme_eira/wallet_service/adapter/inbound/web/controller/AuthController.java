package io.github.guilherme_eira.wallet_service.adapter.inbound.web.controller;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request.*;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.AuthResponse;
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.ErrorResponse; // Import do seu DTO
import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.RegisterResponse;
import io.github.guilherme_eira.wallet_service.adapter.outbound.mapper.AuthMapper;
import io.github.guilherme_eira.wallet_service.application.dto.input.*;
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
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para registro, login, recuperação de senha e MFA")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final ValidateMfaUseCase validateMfaUseCase;
    private final RegisterUseCase registerUseCase;
    private final VerifyUserUseCase verifyUserUseCase;
    private final ResendVerificationUseCase resendVerificationUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final AuthMapper mapper;

    @Operation(summary = "Registrar novo usuário", description = "Cria uma conta de usuário (Comum ou Lojista).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação (@Valid)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito: Email já cadastrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Regra de Negócio: CPF Inválido, Senha Fraca, Email Inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request, UriComponentsBuilder uriComponentsBuilder){
        var newUser = registerUseCase.execute(mapper.toRegisterCommand(request));
        var uri = uriComponentsBuilder.path("/account").buildAndExpand(newUser.id()).toUri();
        return ResponseEntity.created(uri).body(mapper.toRegisterResponse(newUser));
    }

    @Operation(summary = "Verificar conta", description = "Ativa a conta do usuário através do token enviado por e-mail.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta verificada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Token não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Token expirado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/verify")
    public ResponseEntity<RegisterResponse> verifyUser(@Valid @RequestBody VerifyUserRequest req){
        var output = verifyUserUseCase.execute(req.token());
        var response = mapper.toRegisterResponse(output);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Reenviar email de verificação")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Solicitação aceita"),
            @ApiResponse(responseCode = "404", description = "Email não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário já verificado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerificationEmail(@Valid @RequestBody ResendVerificationRequest req){
        resendVerificationUseCase.execute(req.email());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Esqueci minha senha")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Solicitação aceita"),
            @ApiResponse(responseCode = "404", description = "Email não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req){
        forgotPasswordUseCase.execute(req.email());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Redefinir senha")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Token de recuperação não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Token expirado ou Senha nova inválida (fraca)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest req){
        var cmd = new ResetPasswordCommand(req.token(), req.newPassword());
        resetPasswordUseCase.execute(cmd);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Realizar Login", description = "Autentica usuário. Se MFA estiver ativo, retorna flag requiresMfa=true.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticação bem sucedida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas (Email ou senha)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Login Bloqueado (muitas tentativas) ou Usuário não verificado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req){
        var cmd = new LoginCommand(req.email(), req.password());
        var output = loginUseCase.execute(cmd);
        var response = new AuthResponse(output.token(), output.refreshToken(), output.requiresMfa());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Renovar Token (Refresh)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh Token inválido ou expirado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário associado ao token não existe mais", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest req){
        var output = refreshTokenUseCase.execute(req.refreshToken());
        var response = new AuthResponse(output.token(), output.refreshToken(), output.requiresMfa());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Validar MFA (2FA)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código válido, sessão completa iniciada"),
            @ApiResponse(responseCode = "401", description = "Código MFA incorreto", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Código expirado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/validate-code")
    public ResponseEntity<AuthResponse> validateMfa(@Valid @RequestBody ValidateMfaRequest req, @AuthenticationPrincipal UserPrincipal principal){
        var cmd = new ValidateMfaCommand(principal.getId(), req.code());
        var output = validateMfaUseCase.execute(cmd);
        var response = new AuthResponse(output.token(), output.refreshToken(), output.requiresMfa());
        return ResponseEntity.ok().body(response);
    }
}