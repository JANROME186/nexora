package com.nexora.hop.platformfoundation.identityaccess.adapter.in.web;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.nexora.hop.platformfoundation.identityaccess.application.AuthenticationFailedException;
import com.nexora.hop.platformfoundation.identityaccess.application.AccountLockedException;
import com.nexora.hop.platformfoundation.identityaccess.application.AccountSuspendedException;

@RestControllerAdvice(assignableTypes = AuthController.class)
class AuthExceptionHandler {

    @ExceptionHandler(AuthenticationFailedException.class)
    ResponseEntity<ApiErrorResponse> unauthorized(AuthenticationFailedException exception) {
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({AccountLockedException.class, AccountSuspendedException.class})
    ResponseEntity<ApiErrorResponse> forbidden(RuntimeException exception) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    private static ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(status.value(), message, Instant.now()));
    }

    record ApiErrorResponse(int status, String message, Instant occurredAt) {
    }
}
