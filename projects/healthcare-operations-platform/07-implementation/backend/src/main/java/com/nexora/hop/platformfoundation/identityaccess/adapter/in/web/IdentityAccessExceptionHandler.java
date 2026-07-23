package com.nexora.hop.platformfoundation.identityaccess.adapter.in.web;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nexora.hop.platformfoundation.identityaccess.application.IdentityEntityNotFoundException;
import com.nexora.hop.platformfoundation.identityaccess.application.InvalidIdentityCommandException;

@RestControllerAdvice(assignableTypes = { IdentityAccessController.class, AuthController.class })
class IdentityAccessExceptionHandler {

    @ExceptionHandler(IdentityEntityNotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(IdentityEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({ InvalidIdentityCommandException.class, MethodArgumentNotValidException.class })
    ResponseEntity<ApiErrorResponse> badRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "Identity command is invalid.");
    }

    private static ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(status.value(), message, Instant.now()));
    }

    record ApiErrorResponse(int status, String message, Instant occurredAt) {
    }
}
