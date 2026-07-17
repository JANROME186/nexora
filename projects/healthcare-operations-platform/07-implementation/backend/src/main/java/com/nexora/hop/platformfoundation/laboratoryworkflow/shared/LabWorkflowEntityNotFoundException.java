package com.nexora.hop.platformfoundation.laboratoryworkflow.shared;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Raised when a requested laboratory workflow resource is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class LabWorkflowEntityNotFoundException extends RuntimeException {

    public LabWorkflowEntityNotFoundException(String message) {
        super(message);
    }
}
