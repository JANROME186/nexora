package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.List;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Entity of BCM-PLT-010 (ENT-MIG-006). {@link #domainCommandsInvoked} may only reference commands
 * that already exist on an owning domain module (INV-MIG-003); it is intentionally empty in
 * MVP-MOD-008-BE-001 because real cross-module command wiring is MVP-MOD-008-BE-002 scope — no
 * command is invented or invoked prematurely just to appear complete. {@link #checkpoint} is the
 * resume marker for a retried attempt (INV-MIG-004, RN-004); full checkpoint-based resume logic
 * is also MVP-MOD-008-BE-002 scope.
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
