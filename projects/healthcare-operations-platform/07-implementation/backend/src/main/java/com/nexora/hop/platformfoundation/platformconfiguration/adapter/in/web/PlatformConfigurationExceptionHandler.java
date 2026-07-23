package com.nexora.hop.platformfoundation.platformconfiguration.adapter.in.web;

import java.time.Instant;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nexora.hop.platformfoundation.platformconfiguration.application.InvalidPlatformConfigurationCommandException;

@RestControllerAdvice(assignableTypes = PlatformConfigurationController.class)
class PlatformConfigurationExceptionHandler {

    private static final String COMMAND_INVALID = "COMMAND_INVALID";

    @ExceptionHandler({
            InvalidPlatformConfigurationCommandException.class, MethodArgumentNotValidException.class
    })
    ResponseEntity<ApiErrorResponse> badRequest(Exception exception) {
        String message = exception instanceof InvalidPlatformConfigurationCommandException
                ? exception.getMessage()
                : "Platform configuration command is invalid.";
        return error(HttpStatus.BAD_REQUEST, COMMAND_INVALID, message);
    }

    private static ResponseEntity<ApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(status.value(), code, messageKeyFor(code), message, Instant.now()));
    }

    /** Deterministic catalog-key naming convention: {@code platformconfiguration.error.<code, lowercase>}. */
    private static String messageKeyFor(String code) {
        return "platformconfiguration.error." + code.toLowerCase(Locale.ROOT);
    }

    record ApiErrorResponse(int status, String code, String messageKey, String message, Instant occurredAt) {
    }
}
