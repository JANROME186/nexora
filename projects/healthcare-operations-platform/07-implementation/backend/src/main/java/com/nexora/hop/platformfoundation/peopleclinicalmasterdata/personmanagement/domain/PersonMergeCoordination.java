package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain;

import java.time.Instant;

/**
 * Cross-context merge coordination record (BCM-PER-001 custom implementation point
 * CUS-PER-001-05). Person is not an owning aggregate, so this record only tracks the coordination
 * decision between two candidate records; the actual aggregate mutation is delegated to the owning
 * bounded context. When both records are patients, the coordination triggers the BCM-PER-002
 * {@code mergePatient} aggregate command (RN-005) and {@link #patientMergeApplied()} is set to
 * {@code true}. Doctor has no merge concept in the current business model (BCM-PER-003 does not
 * declare a mergeDoctor operation), so doctor-involved coordinations are recorded as a decision
 * only, without an aggregate mutation.
 */
public record PersonMergeCoordination(
        String coordinationId,
        String tenantId,
        String sourceKind,
        String sourceRecordId,
        String targetKind,
        String targetRecordId,
        String status,
        boolean patientMergeApplied,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_PATIENTS_MERGED = "patients_merged";
    public static final String STATUS_RECORDED_NO_AGGREGATE_OPERATION = "recorded_no_aggregate_operation";
}
