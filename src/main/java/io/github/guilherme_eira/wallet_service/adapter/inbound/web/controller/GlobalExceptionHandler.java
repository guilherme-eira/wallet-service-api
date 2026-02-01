package io.github.guilherme_eira.wallet_service.adapter.inbound.web.controller;

import io.github.guilherme_eira.wallet_service.adapter.inbound.web.dto.response.ErrorResponse;
import io.github.guilherme_eira.wallet_service.application.exception.*;
import io.github.guilherme_eira.wallet_service.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            WalletNotFoundException.class,
            PasswordResetTokenNotFoundException.class,
            VerificationTokenNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler({
            InsufficientBalanceException.class,
            InvalidAmountException.class,
            InvalidEmailException.class,
            InvalidPasswordException.class,
            InvalidTaxIdException.class,
            InvalidTransactionPinException.class,
            ExpiredPasswordResetTokenException.class,
            ExpiredVerificationTokenException.class,
            LimitExceededException.class,
            MerchantTransferNotAllowedException.class,
            TransferToSameWalletException.class,
            IncorrectCurrentPasswordException.class,
            IncorrectPinException.class,
            TransactionNotAuthorizedException.class,
            TransferBlockedException.class,
            NonZeroBalanceException.class,
            ReceiverNotVerifiedException.class
    })
    public ResponseEntity<ErrorResponse> handleUnprocessableEntity(RuntimeException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex);
    }

    @ExceptionHandler({
            LoginBlockedException.class,
            UserNotVerifiedException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler({
            IncorrectMfaCodeException.class,
            AuthTokenVerificationException.class,
            IncorrectCredentialsException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorized(Exception ex) {
        String message = ex instanceof IncorrectCredentialsException ? "E-mail ou senha inválidos" : ex.getMessage();
        return buildResponse(HttpStatus.UNAUTHORIZED, message, ex.getClass().getSimpleName());
    }

    @ExceptionHandler({
            AuthTokenGenerationException.class,
            Exception.class
    })
    public ResponseEntity<ErrorResponse> handleInternalError(Exception ex) {
        log.error("ERRO INTERNO NÃO TRATADO: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado.", "INTERNAL_SERVER_ERROR");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ErrorResponse.ValidationError> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> ErrorResponse.ValidationError.builder()
                        .field(e.getField())
                        .message(e.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .code("VALIDATION_ERROR")
                        .message("Dados de entrada inválidos")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timestamp(LocalDateTime.now())
                        .details(details)
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonError(HttpMessageNotReadableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido (JSON malformado).", "INVALID_JSON_FORMAT");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, Exception ex) {
        return buildResponse(status, ex.getMessage(), ex.getClass().getSimpleName());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String exceptionClassName) {
        String code = toSnakeCase(exceptionClassName.replace("Exception", ""));

        ErrorResponse response = ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    private String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }
}
