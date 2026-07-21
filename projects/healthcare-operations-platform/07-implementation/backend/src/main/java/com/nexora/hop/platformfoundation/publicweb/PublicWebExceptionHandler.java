package com.nexora.hop.platformfoundation.publicweb;

import java.time.Instant;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shared exception mapping for every COM-MOD-011 public-classified controller. Every response
 * carries the same {@code status/code/messageKey/message/occurredAt} shape used by
 * BCM-PLT-004/005 (further reducing TD-I18N-002 by keeping the public error surface fully keyed
 * against the i18n catalog).
 */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.publicweb")
public class PublicWebExceptionHandler {

    @ExceptionHandler(PublicWebException.class)
    ResponseEntity<PublicApiErrorResponse> handle(PublicWebException exception) {
        return error(exception.status(), exception.code(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<PublicApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        return error(HttpStatus.BAD_REQUEST, PublicWebErrorCodes.PUBLIC_APPOINTMENT_REQUEST_INVALID,
                "Public request is invalid.");
    }

    private static ResponseEntity<PublicApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new PublicApiErrorResponse(
                status.value(), code, messageKeyFor(code), message, Instant.now()));
    }

    /** Deterministic catalog-key naming convention: {@code public.error.<code, lowercase>}. */
    static String messageKeyFor(String code) {
        return "public.error." + code.toLowerCase(Locale.ROOT);
    }

    public record PublicApiErrorResponse(
            int status, String code, String messageKey, String message, Instant occurredAt) {
    }
}
