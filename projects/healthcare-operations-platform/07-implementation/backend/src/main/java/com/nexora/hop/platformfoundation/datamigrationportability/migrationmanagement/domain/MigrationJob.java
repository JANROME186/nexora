package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Root aggregate of BCM-PLT-010 Open Data Ingestion and Migration (ENT-MIG-001, AGG-016
 * MigrationJob per aggregate-catalog.yaml). Never writes directly to a business aggregate's
 * storage (INV-MIG-003); {@code executing}/{@code reconciled} only reachable through the
 * checkpointed {@code ImportExecution} lifecycle, real domain-command invocation deferred to
 * MVP-MOD-008-BE-002.
 */
public record MigrationJob(
        String migrationJobId,
        String tenantId,
        String laboratoryId,
        String sourceSystemName,
        String status,
        AuditMetadata audit) {

    public static final String STATUS_CREATED = "created";
    public static final String STATUS_PACKAGE_RECEIVED = "package_received";
    public static final String STATUS_MAPPED = "mapped";
    public static final String STATUS_DRY_RUN_VALIDATED = "dry_run_validated";
    public static final String STATUS_APPROVED = "approved";
    public static final String STATUS_EXECUTING = "executing";
    public static final String STATUS_RECONCILED = "reconciled";
    public static final String STATUS_FAILED = "failed";
}
