package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.Map;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Entity of BCM-PLT-010 (ENT-MIG-005). Every status transition of {@link MigrationJob} produces
 * or updates a reconciliation report (INV-MIG-005, RN-005).
 */
public record ReconciliationReport(
        String reconciliationReportId,
        String migrationJobId,
        String phase,
        Map<String, Integer> importedCounts,
        Map<String, Integer> rejectedCounts,
        Map<String, Integer> skippedCounts,
        Map<String, Integer> warningCounts,
        AuditMetadata audit) {

    public static final String PHASE_PRE_IMPORT = "pre_import";
    public static final String PHASE_POST_IMPORT = "post_import";
}
