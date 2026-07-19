package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.List;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Entity of BCM-PLT-010 (ENT-MIG-006). {@link #domainCommandsInvoked} holds the stable command
 * identifier {@code MigrationDomainCommandPort} returned for each entity category successfully
 * delegated this execution chain (INV-MIG-003: the port is the only interaction point, so no
 * command ever writes directly to a business aggregate). {@link #checkpoint} is the
 * delimiter-joined list of entity categories already completed, letting
 * {@code MigrationManagementService#retryImportExecution} resume only the categories that have
 * not yet succeeded instead of re-invoking them (INV-MIG-004, RN-004).
 */
public record ImportExecution(
        String executionId,
        String migrationJobId,
        String importBatchId,
        int attemptNumber,
        List<String> domainCommandsInvoked,
        String checkpoint,
        String status,
        AuditMetadata audit) {

    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_RETRIED = "retried";
}
