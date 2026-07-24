package com.nexora.hop.platformfoundation.marketplaceentitlements.shared;

import java.time.Instant;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shared exception mapping for BCM-PLT-011 Product Marketplace and Entitlements controllers.
 * Every response carries a first-class {@code code} field plus a {@code messageKey}
 * ({@code marketplace.error.*} in {@code i18n/messages*.properties}), mirroring
 * {@code IntegrationExceptionHandler}.
 */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.marketplaceentitlements")
public class MarketplaceExceptionHandler {

    @ExceptionHandler(MarketplaceEntityNotFoundException.class)
    ResponseEntity<MarketplaceApiErrorResponse> notFound(MarketplaceEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.code(), exception.getMessage());
    }

    @ExceptionHandler({InvalidMarketplaceCommandException.class, MethodArgumentNotValidException.class})
    ResponseEntity<MarketplaceApiErrorResponse> badRequest(Exception exception) {
        String code = exception instanceof InvalidMarketplaceCommandException invalid
                ? invalid.code()
                : MarketplaceErrorCodes.MARKETPLACE_COMMAND_INVALID;
        String message = exception instanceof InvalidMarketplaceCommandException invalid
                ? invalid.getMessage()
                : "Marketplace command is invalid.";
        return error(HttpStatus.BAD_REQUEST, code, message);
    }

    @ExceptionHandler(MarketplaceConflictException.class)
    ResponseEntity<MarketplaceApiErrorResponse> conflict(MarketplaceConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.code(), exception.getMessage());
    }

    private static ResponseEntity<MarketplaceApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new MarketplaceApiErrorResponse(
                status.value(), code, messageKeyFor(code), message, Instant.now()));
    }

    /** Deterministic catalog-key naming convention: {@code marketplace.error.<code, lowercase>}. */
    public static String messageKeyFor(String code) {
        return "marketplace.error." + code.toLowerCase(Locale.ROOT);
    }

    public record MarketplaceApiErrorResponse(
            int status, String code, String messageKey, String message, Instant occurredAt) {
    }
}
