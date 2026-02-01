package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.request;

import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Formulário de cadastro de novo usuário")
public record RegisterRequest(

        @Schema(description = "Nome completo", example = "Guilherme Eira")
        @NotBlank(message = "O campo 'name' é obrigatório")
        String name,

        @Schema(description = "CPF. Será usado como chave de pagamento.", example = "605.804.810-93")
        @NotBlank(message = "O campo 'taxId' é obrigatório")
        String taxId,

        @Schema(description = "E-mail para login e notificações", example = "guilherme@email.com")
        @NotBlank(message = "O campo 'email' é obrigatório")
        @Email(message = "Deve ser um e-mail válido")
        String email,

        @Schema(description = "Senha forte (mínimo 8 caracteres)", example = "S3nhaForte!123")
        @NotBlank(message = "O campo 'password' é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String password,

        @Schema(description = "Tipo da conta (COMMON = Usuário, MERCHANT = Lojista)", example = "COMMON")
        @NotNull(message = "O campo 'type' é obrigatório")
        UserType type,

        @Schema(description = "PIN numérico de 4 dígitos para transações financeiras", example = "1246")
        @NotBlank(message = "O campo 'transactionPin' é obrigatório")
        @Size(min = 4, max = 4, message = "O PIN deve ter exatamente 4 dígitos")
        String transactionPin
) {
}