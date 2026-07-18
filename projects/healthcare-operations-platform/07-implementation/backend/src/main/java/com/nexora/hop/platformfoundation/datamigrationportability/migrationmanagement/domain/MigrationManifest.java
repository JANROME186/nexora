package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Value object mirroring {@code manifest.yaml} per the HOP Open Data Ingestion Standard
 * (NXF-ODI-STD-001) and open-data-ingestion-contract.yaml. Every field here is a required
 * manifest field per the contract; a package that cannot provide all of them is rejected before
 * parsing continues (RN-001, INV-MIG-001).
 */
public record MigrationManifest(
        String sourceSystemName,
        String sourceSystemVersion,
        String exportingOrganization,
        Instant exportDatetime,
        String exportTimezone,
        String exportedBy,
        String contactEmail,
        List<String> files,
        Map<String, Integer> entityCounts,
        String checksumAlgorithm,
        Map<String, String> checksums,
        List<String> declaredFormats,
        String declaredEncoding) {
}
