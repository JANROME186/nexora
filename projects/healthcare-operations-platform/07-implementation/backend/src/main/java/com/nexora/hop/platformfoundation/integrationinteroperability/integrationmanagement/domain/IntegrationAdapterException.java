package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

/**
 * Thrown by {@link IntegrationAdapterPort} implementations. Carries a canonical error code only
 * (RN-002) — implementations must never leak raw provider-specific error text through this
 * exception.
 */
public class IntegrationAdapterException extends RuntimeException {

    private final String canonicalErrorCode;

    public IntegrationAdapterException(String message, String canonicalErrorCode) {
        super(message);
        this.canonicalErrorCode = canonicalErrorCode;
    }

    public String canonicalErrorCode() {
        return canonicalErrorCode;
    }
}
