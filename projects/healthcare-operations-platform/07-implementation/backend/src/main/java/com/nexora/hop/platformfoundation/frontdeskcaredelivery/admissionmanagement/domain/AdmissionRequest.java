package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain;

import java.time.Instant;

/**
 * Order-intake draft assembled at the front desk prior to committing a DiagnosticOrder
 * (ENT-ADM-001). Commit delegates to {@code DiagnosticOrderManagementService} (BCM-LAB-001)
 * rather than persisting order state itself (RN-004).
 */
public record AdmissionRequest(
        String admissionId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String visitId,
        String patientId,
        String doctorId,
        String clinicalNotesDraft,
        boolean consentConfirmed,
        boolean sampleRequirementsAcknowledged,
        String admissionStatus,
        String createdOrderId,
        String rejectionReason,
        String actorId,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_READY_FOR_ORDER = "ready_for_order";
    public static final String STATUS_ORDER_CREATED = "order_created";
    public static final String STATUS_REJECTED = "rejected";
}
