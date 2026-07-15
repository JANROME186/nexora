package com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shared REST exception mapping for every controller in the frontdeskcaredelivery module. One
 * handler keeps the response contract consistent across BCM-ATT-001/003/004/006 and BCM-LAB-001
 * without duplicating boilerplate advice per capability.
 */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.frontdeskcaredelivery")
public class FrontDeskExceptionHandler {

    @ExceptionHandler(FrontDeskEntityNotFoundException.class)
    ResponseEntity<FrontDeskApiErrorResponse> notFound(FrontDeskEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({ InvalidFrontDeskCommandException.class, MethodArgumentNotValidException.class })
    ResponseEntity<FrontDeskApiErrorResponse> badRequest(Exception exception) {
        String message = exception.getMessage();
        return error(HttpStatus.BAD_REQUEST,
                (message == null || message.isBlank()) ? "Front desk command is invalid." : message);
    }

    @ExceptionHandler(FrontDeskConflictException.class)
    ResponseEntity<FrontDeskApiErrorResponse> conflict(FrontDeskConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    private static ResponseEntity<FrontDeskApiErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new FrontDeskApiErrorResponse(status.value(), message, Instant.now()));
    }

    public record FrontDeskApiErrorResponse(int status, String message, Instant occurredAt) {
    }
}
