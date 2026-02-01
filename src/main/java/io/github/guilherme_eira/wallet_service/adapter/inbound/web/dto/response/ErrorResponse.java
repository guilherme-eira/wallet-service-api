package io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura padrão de erros da API")
public class ErrorResponse {

    @Schema(description = "Código de erro (Machine Readable)", example = "USER_NOT_FOUND")
    private final String code;

    @Schema(description = "Mensagem descritiva (Human Readable)", example = "Usuário não encontrado")
    private final String message;

    @Schema(description = "Status HTTP", example = "404")
    private final int status;

    @Schema(description = "Carimbo de tempo do erro")
    private final LocalDateTime timestamp;

    @Schema(description = "Lista de violações de campos (Apenas em erro 400 Bad Request)")
    private final List<ValidationError> details;

    @Getter
    @Builder
    @Schema(description = "Detalhe de validação de campo")
    public static class ValidationError {

        @Schema(description = "Nome do campo inválido", example = "email")
        private final String field;

        @Schema(description = "Motivo do erro", example = "Formato de e-mail inválido")
        private final String message;
    }
}