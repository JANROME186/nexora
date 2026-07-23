package com.nexora.hop.platformfoundation.organizationmanagement.adapter.in.web;

import java.time.Instant;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nexora.hop.platformfoundation.organizationmanagement.application.InvalidOrganizationCommandException;
import com.nexora.hop.platformfoundation.organizationmanagement.application.OrganizationEntityNotFoundException;
import com.nexora.hop.platformfoundation.organizationmanagement.application.TenantCodeConflictException;

@RestControllerAdvice(assignableTypes = OrganizationManagementController.class)
class OrganizationManagementExceptionHandler {

    private static final String ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";
    private static final String COMMAND_INVALID = "ORGANIZATION_COMMAND_INVALID";
    private static final String TENANT_CODE_CONFLICT = "TENANT_CODE_CONFLICT";

    @ExceptionHandler(OrganizationEntityNotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(OrganizationEntityNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, ENTITY_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(TenantCodeConflictException.class)
    ResponseEntity<ApiErrorResponse> conflict(TenantCodeConflictException exception) {
        return error(HttpStatus.CONFLICT, TENANT_CODE_CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({ InvalidOrganizationCommandException.class, MethodArgumentNotValidException.class })
    ResponseEntity<ApiErrorResponse> badRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, COMMAND_INVALID, "Organization command is invalid.");
    }

    private static ResponseEntity<ApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(status.value(), code, messageKeyFor(code), message, Instant.now()));
    }

    /** Deterministic catalog-key naming convention: {@code organizationmanagement.error.<code, lowercase>}. */
    private static String messageKeyFor(String code) {
        return "organizationmanagement.error." + code.toLowerCase(Locale.ROOT);
    }

    record ApiErrorResponse(int status, String code, String messageKey, String message, Instant occurredAt) {
    }
}
