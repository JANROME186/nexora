package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.application;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps {@link ResultHistoryAccessDeniedException} to a 403 response for ResultHistoryController. */
@RestControllerAdvice(assignableTypes = ResultHistoryController.class)
public class ResultHistoryExceptionHandler {

    @ExceptionHandler(ResultHistoryAccessDeniedException.class)
    ResponseEntity<ResultHistoryApiErrorResponse> forbidden(ResultHistoryAccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResultHistoryApiErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage(), Instant.now()));
    }

    public record ResultHistoryApiErrorResponse(int status, String message, Instant occurredAt) {
    }
}
