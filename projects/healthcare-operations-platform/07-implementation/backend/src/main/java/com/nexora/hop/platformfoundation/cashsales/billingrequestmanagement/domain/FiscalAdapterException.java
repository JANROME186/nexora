package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain;

/**
 * Thrown by {@link FiscalAdapterPort} implementations when the fiscal provider rejects a request
 * or an infrastructure error prevents delivery. Callers inspect {@link #isRetryable()} to decide
 * whether to update the invoice request to {@code failed} and offer a manual retry, or to mark it
 * as terminal.
 */
public class FiscalAdapterException extends RuntimeException {

    private final boolean retryable;
    private final String normalizedErrorCode;

    public FiscalAdapterException(String message, String normalizedErrorCode, boolean retryable) {
        super(message);
        this.normalizedErrorCode = normalizedErrorCode;
        this.retryable = retryable;
    }

    /**
     * Returns {@code true} when the caller may retry the operation after a delay.
     * Returns {@code false} when the error is terminal and the billing request should be marked
     * as definitively failed.
     */
    public boolean isRetryable() {
        return retryable;
    }

    /**
     * Returns a normalised, provider-agnostic error code for audit and observability.
     */
    public String getNormalizedErrorCode() {
        return normalizedErrorCode;
    }
}
