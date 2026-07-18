package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.List;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Entity of BCM-PLT-010 (ENT-MIG-004), the requirements-registry-sanctioned name for
 * aggregate-catalog.yaml's {@code ValidationReport}. {@link #passed} gates
 * {@code approveImport}/{@code commitImport} (RN-002, INV-MIG-002).
 */
public record ImportValidationReport(
        String reportId,
        String importBatchId,
        List<String> structuralErrors,
        List<String> rowLevelErrors,
        List<String> rowLevelWarnings,
        List<String> validationCategoriesEvaluated,
        boolean passed,
        AuditMetadata audit) {
}
