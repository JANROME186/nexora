package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

/**
 * Thrown by {@link MigrationDomainCommandPort} implementations. Carries a canonical error code
 * only, mirroring {@code IntegrationAdapterException}/{@code FiscalAdapterException} — an
 * implementation must never leak raw provider-specific error text through this exception.
 */
public class MigrationAdapterException extends RuntimeException {

    private final String canonicalErrorCode;

    public MigrationAdapterException(String message, String canonicalErrorCode) {
        super(message);
        this.canonicalErrorCode = canonicalErrorCode;
    }

    public String canonicalErrorCode() {
        return canonicalErrorCode;
    }
}
