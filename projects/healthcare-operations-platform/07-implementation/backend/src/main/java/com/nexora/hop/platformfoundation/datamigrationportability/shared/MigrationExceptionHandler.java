package com.nexora.hop.platformfoundation.datamigrationportability.shared;

import java.time.Instant;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shared exception mapping for BCM-PLT-010 Open Data Ingestion and Migration controllers. Every
 * response carries a first-class {@code code} field, consistent with BCM-PLT-004/BCM-PLT-005, plus
 * a {@code messageKey} (i18n/messages catalog key, see {@code migration.error.*} in
 * {@code i18n/messages*.properties}) so a client can resolve a localized message independently of
 * the always-English {@code message} field (further reduces TD-I18N-002).
 */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.datamigrationportability")
public class MigrationExceptionHandler {

    @ExceptionHandler(MigrationEntityNotFoundException.class)
    ResponseEntity<MigrationApiErrorResponse> notFound(MigrationEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.code(), exception.getMessage());
    }

    @ExceptionHandler({InvalidMigrationCommandException.class, MethodArgumentNotValidException.class})
    ResponseEntity<MigrationApiErrorResponse> badRequest(Exception exception) {
        String code = exception instanceof InvalidMigrationCommandException invalid
                ? invalid.code()
                : MigrationErrorCodes.MIGRATION_COMMAND_INVALID;
        String message = exception instanceof InvalidMigrationCommandException invalid
                ? invalid.getMessage()
                : "Migration command is invalid.";
        return error(HttpStatus.BAD_REQUEST, code, message);
    }

    @ExceptionHandler(MigrationConflictException.class)
    ResponseEntity<MigrationApiErrorResponse> conflict(MigrationConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.code(), exception.getMessage());
    }

    private static ResponseEntity<MigrationApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new MigrationApiErrorResponse(
                status.value(), code, messageKeyFor(code), message, Instant.now()));
    }

    /** Deterministic catalog-key naming convention: {@code migration.error.<code, lowercase>}. */
    static String messageKeyFor(String code) {
        return "migration.error." + code.toLowerCase(Locale.ROOT);
    }

    public record MigrationApiErrorResponse(
            int status, String code, String messageKey, String message, Instant occurredAt) {
    }
}
