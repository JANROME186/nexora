package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shared REST exception mapping for every controller in the peopleclinicalmasterdata module. One
 * handler keeps the response contract consistent across BCM-PER-001/002/003 and BCM-ATT-002
 * without duplicating boilerplate advice per capability.
 */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.peopleclinicalmasterdata")
public class PeopleExceptionHandler {

    @ExceptionHandler(PeopleEntityNotFoundException.class)
    ResponseEntity<PeopleApiErrorResponse> notFound(PeopleEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), null, null);
    }

    @ExceptionHandler({ InvalidPeopleCommandException.class, MethodArgumentNotValidException.class })
    ResponseEntity<PeopleApiErrorResponse> badRequest(Exception exception) {
        String message = exception.getMessage();
        return error(HttpStatus.BAD_REQUEST,
                (message == null || message.isBlank()) ? "People command is invalid." : message,
                null, null);
    }

    @ExceptionHandler(PeopleConflictException.class)
    ResponseEntity<PeopleApiErrorResponse> conflict(PeopleConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), null, null);
    }

    @ExceptionHandler(PeopleCustomRuleNotImplementedException.class)
    ResponseEntity<PeopleApiErrorResponse> notImplemented(PeopleCustomRuleNotImplementedException exception) {
        return error(HttpStatus.NOT_IMPLEMENTED, exception.getMessage(), exception.ruleId(), exception.backlogItem());
    }

    private static ResponseEntity<PeopleApiErrorResponse> error(
            HttpStatus status,
            String message,
            String ruleId,
            String backlogItem) {
        return ResponseEntity.status(status)
                .body(new PeopleApiErrorResponse(status.value(), message, ruleId, backlogItem, Instant.now()));
    }

    public record PeopleApiErrorResponse(
            int status,
            String message,
            String ruleId,
            String backlogItem,
            Instant occurredAt) {
    }
}
