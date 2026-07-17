package com.nexora.hop.platformfoundation.laboratoryworkflow.shared;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Raised when a laboratory workflow command argument is invalid or missing.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLabWorkflowCommandException extends RuntimeException {

    public InvalidLabWorkflowCommandException(String message) {
        super(message);
    }
}
