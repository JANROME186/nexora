package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain;

import java.time.Instant;

/**
 * Diagnostic order aggregate root (AGG-007) owned by the {@code orders-samples} bounded context.
 * Modeled by {@code bcm-lab-001-diagnostic-order-management/business-model.yaml} ENT-ORD-001. Only
 * this capability may mutate this aggregate; Appointment Scheduling, Reception Management and
 * Admission Management orchestrate around it and reference the result by {@code orderId} (RN-004).
 * Order lines are a separate child collection, retrieved through
 * {@link DiagnosticOrderRepository#findOrderLines(String)}, mirroring how Patient representatives
 * are modeled as a sibling table rather than an embedded list.
 */
public record DiagnosticOrder(
        String orderId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String intakeChannel,
        String sourceReferenceId,
        PatientSnapshot patientSnapshot,
        DoctorSnapshot doctorSnapshot,
        BranchSnapshot branchSnapshot,
        String clinicalNotes,
        OrderPricingSnapshot pricingSnapshot,
        String status,
        String cancellationReason,
        String actorId,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String CHANNEL_WALK_IN = "walk_in";
    public static final String CHANNEL_APPOINTMENT = "appointment";
    public static final String CHANNEL_ADMISSION = "admission";
    public static final String CHANNEL_QUOTATION_CONVERSION = "quotation_conversion";
    public static final String CHANNEL_PORTAL_REQUEST_LATER = "portal_request_later";

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PRICED = "priced";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_COMPLETED = "completed";
}
