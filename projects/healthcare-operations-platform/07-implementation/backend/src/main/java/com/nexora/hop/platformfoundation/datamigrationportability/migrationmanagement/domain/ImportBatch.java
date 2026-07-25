package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.Map;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Entity of BCM-PLT-010 (ENT-MIG-002), the requirements-registry-sanctioned name for
 * aggregate-catalog.md's {@code SourceDataset} (AGG-016.owns). {@code storedPackageReference}
 * points at a {@code StoredDocument} via BCM-PLT-008's {@code DocumentStoragePort} (PORT-MIG-001)
 * rather than a bespoke storage boundary.
 */
public record ImportBatch(
        String importBatchId,
        String migrationJobId,
        MigrationManifest manifest,
        String storedPackageReference,
        Map<String, Integer> entityCounts,
        AuditMetadata audit) {
}
