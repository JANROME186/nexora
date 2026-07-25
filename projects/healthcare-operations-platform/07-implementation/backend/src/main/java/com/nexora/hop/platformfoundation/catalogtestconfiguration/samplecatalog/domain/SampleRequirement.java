package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain;

import java.math.BigDecimal;
import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/**
 * The sample specification required to run a test (BCM-SVC-007, ENT-SMP-002).
 *
 * <p>Model gap: business-model.md does not declare tenantId/laboratoryId fields on
 * SampleRequirement (unlike every other catalog entity). This implementation adds them for
 * tenant/laboratory scoping consistency with the rest of the Diagnostic Catalog, following the
 * minimum-compatible-option rule; see traceability notes for MVP-MOD-002-BE-001.</p>
 */
public record SampleRequirement(
        String requirementId,
        String tenantId,
        String laboratoryId,
        String sampleTypeRefId,
        BigDecimal minVolumeMl,
        String containerRefId,
        LocalizedText handlingInstructions,
        String storageTemperature,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";

    public static final String STORAGE_AMBIENT = "ambient";
    public static final String STORAGE_REFRIGERATED = "refrigerated";
    public static final String STORAGE_FROZEN = "frozen";
    public static final String STORAGE_DEEP_FROZEN = "deep_frozen";
}
