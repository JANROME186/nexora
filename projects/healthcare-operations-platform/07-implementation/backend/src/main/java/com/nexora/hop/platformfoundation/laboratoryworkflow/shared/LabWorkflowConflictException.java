package com.nexora.hop.platformfoundation.laboratoryworkflow.shared;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Raised when a laboratory workflow operation conflicts with current aggregate state.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class LabWorkflowConflictException extends RuntimeException {

    public LabWorkflowConflictException(String message) {
        super(message);
    }
}
