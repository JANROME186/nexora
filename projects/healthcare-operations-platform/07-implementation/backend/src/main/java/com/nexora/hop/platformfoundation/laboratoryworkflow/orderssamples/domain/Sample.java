package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.time.Instant;
import java.util.List;

/**
 * Sample aggregate root (AGG-008, BCM-LAB-002).
 *
 * <p>This capability (BCM-LAB-002) defines the complete field structure and creates the aggregate.
 * BCM-LAB-003 holds delegated authority to mutate {@code labelInfo}.
 * BCM-LAB-005 holds delegated authority to mutate {@code receptionRecord}, {@code rejectionReason}
 * and {@code status} for reception-stage transitions.
 * No other capability or bounded context may write Sample persistence directly (INV-COL-003).
 *
 * <p>Invariants from business-model.yaml:
 * <ul>
 *   <li>INV-COL-001: Must always reference a valid order line with patient and requirement snapshots.</li>
 *   <li>INV-COL-002: Cannot exist without at least one chain-of-custody event (collected appended at creation).</li>
 *   <li>INV-COL-003: Only BCM-LAB-002/003/005 may mutate; others are forbidden.</li>
 *   <li>INV-COL-004: A rejected sample is terminal (no further non-disposal transitions).</li>
 *   <li>INV-COL-005: Chain-of-custody list is append-only; entries are immutable.</li>
 * </ul>
 */
public record Sample(
        String sampleId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String orderId,
        String orderLineId,
        PatientIdentitySnapshot patientSnapshot,
        SampleRequirementSnapshot sampleRequirementSnapshot,
        SampleCollectionData collectionData,
        SpecimenLabelInfo labelInfo,
        SampleReceptionRecord receptionRecord,
        SampleRejectionReason rejectionReason,
        SampleStatus status,
        List<ChainOfCustodyEvent> chainOfCustody,
        Instant createdAt,
        Instant updatedAt) {
}
