package com.nexora.hop.platformfoundation.catalogtestconfiguration.shared;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shared exception mapping for every Diagnostic Catalog controller (BCM-SVC-001..007, BCM-SVC-009).
 * A single handler avoids repeating the same generatable mapping logic per capability, consistent
 * with the MDPE rule against writing repetitive artifacts manually.
 */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.catalogtestconfiguration")
public class CatalogExceptionHandler {

    @ExceptionHandler(CatalogEntityNotFoundException.class)
    ResponseEntity<CatalogApiErrorResponse> notFound(CatalogEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), null, null);
    }

    @ExceptionHandler({ InvalidCatalogCommandException.class, MethodArgumentNotValidException.class })
    ResponseEntity<CatalogApiErrorResponse> badRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "Catalog command is invalid.", null, null);
    }

    @ExceptionHandler(CatalogCustomRuleNotImplementedException.class)
    ResponseEntity<CatalogApiErrorResponse> notImplemented(CatalogCustomRuleNotImplementedException exception) {
        return error(HttpStatus.NOT_IMPLEMENTED, exception.getMessage(), exception.ruleId(), exception.backlogItem());
    }

    private static ResponseEntity<CatalogApiErrorResponse> error(
            HttpStatus status,
            String message,
            String ruleId,
            String backlogItem) {
        return ResponseEntity.status(status)
                .body(new CatalogApiErrorResponse(status.value(), message, ruleId, backlogItem, Instant.now()));
    }

    public record CatalogApiErrorResponse(
            int status,
            String message,
            String ruleId,
            String backlogItem,
            Instant occurredAt) {
    }
}
