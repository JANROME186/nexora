package com.nexora.hop.platformfoundation.organizationmanagement.adapter.in.web;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nexora.hop.platformfoundation.organizationmanagement.application.InvalidOrganizationCommandException;
import com.nexora.hop.platformfoundation.organizationmanagement.application.OrganizationEntityNotFoundException;

@RestControllerAdvice(assignableTypes = OrganizationManagementController.class)
class OrganizationManagementExceptionHandler {

    @ExceptionHandler(OrganizationEntityNotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(OrganizationEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({ InvalidOrganizationCommandException.class, MethodArgumentNotValidException.class })
    ResponseEntity<ApiErrorResponse> badRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "Organization command is invalid.");
    }

    private static ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(status.value(), message, Instant.now()));
    }

    record ApiErrorResponse(int status, String message, Instant occurredAt) {
    }
}
