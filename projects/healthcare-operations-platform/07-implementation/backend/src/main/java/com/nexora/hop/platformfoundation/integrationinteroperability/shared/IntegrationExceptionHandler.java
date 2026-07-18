package com.nexora.hop.platformfoundation.integrationinteroperability.shared;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shared exception mapping for BCM-PLT-004 Integration Management and BCM-PLT-005 API Management
 * controllers. Every response carries a first-class {@code code} field (RFC7807-inspired, per
 * each capability's {@code openapi-source.yaml error_model}), the first HOP error shape to do so
 * (TD-I18N-002).
 */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.integrationinteroperability")
public class IntegrationExceptionHandler {

    @ExceptionHandler(IntegrationEntityNotFoundException.class)
    ResponseEntity<IntegrationApiErrorResponse> notFound(IntegrationEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.code(), exception.getMessage());
    }

    @ExceptionHandler({InvalidIntegrationCommandException.class, MethodArgumentNotValidException.class})
    ResponseEntity<IntegrationApiErrorResponse> badRequest(Exception exception) {
        String code = exception instanceof InvalidIntegrationCommandException invalid
                ? invalid.code()
                : "INTEGRATION_COMMAND_INVALID";
        String message = exception instanceof InvalidIntegrationCommandException invalid
                ? invalid.getMessage()
                : "Integration or API management command is invalid.";
        return error(HttpStatus.BAD_REQUEST, code, message);
    }

    @ExceptionHandler(IntegrationConflictException.class)
    ResponseEntity<IntegrationApiErrorResponse> conflict(IntegrationConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.code(), exception.getMessage());
    }

    private static ResponseEntity<IntegrationApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new IntegrationApiErrorResponse(
                status.value(), code, message, Instant.now()));
    }

    public record IntegrationApiErrorResponse(int status, String code, String message, Instant occurredAt) {
    }
}
