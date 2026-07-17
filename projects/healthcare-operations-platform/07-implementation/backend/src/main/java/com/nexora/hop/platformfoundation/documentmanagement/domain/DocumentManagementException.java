package com.nexora.hop.platformfoundation.documentmanagement.domain;

/**
 * Custom exception representing document management failures to avoid throwing raw RuntimeExceptions.
 */
public class DocumentManagementException extends RuntimeException {

    public DocumentManagementException(String message) {
        super(message);
    }

    public DocumentManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
