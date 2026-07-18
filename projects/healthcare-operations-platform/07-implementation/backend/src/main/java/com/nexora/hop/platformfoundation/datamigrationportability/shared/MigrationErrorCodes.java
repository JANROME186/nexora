package com.nexora.hop.platformfoundation.datamigrationportability.shared;

/**
 * Canonical structured error codes for BCM-PLT-010 Open Data Ingestion and Migration, matching
 * {@code openapi-source.yaml error_model.domain_errors} exactly.
 */
public final class MigrationErrorCodes {

    public static final String MIGRATION_MANIFEST_INVALID_OR_MISSING = "MIGRATION_MANIFEST_INVALID_OR_MISSING";
    public static final String MIGRATION_DRY_RUN_NOT_PASSED = "MIGRATION_DRY_RUN_NOT_PASSED";
    public static final String MIGRATION_DIRECT_AGGREGATE_WRITE_ATTEMPTED =
            "MIGRATION_DIRECT_AGGREGATE_WRITE_ATTEMPTED";
    public static final String MIGRATION_RETRY_CHECKPOINT_MISMATCH = "MIGRATION_RETRY_CHECKPOINT_MISMATCH";
    public static final String MIGRATION_SCOPE_MISMATCH = "MIGRATION_SCOPE_MISMATCH";
    public static final String MIGRATION_COMMAND_INVALID = "MIGRATION_COMMAND_INVALID";

    private MigrationErrorCodes() {
    }
}
