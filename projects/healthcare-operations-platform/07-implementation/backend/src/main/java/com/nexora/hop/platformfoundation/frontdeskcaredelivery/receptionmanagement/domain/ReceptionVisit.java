package com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain;

import java.time.Instant;

/**
 * Front-desk visit record (ENT-REC-001) used to confirm identity and queue the patient for
 * admission. Does not mutate Patient (BCM-PER-002) or DiagnosticOrder (BCM-LAB-001) state.
 */
public record ReceptionVisit(
        String visitId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String patientId,
        String linkedAppointmentId,
        String intakeChannel,
        boolean identityConfirmed,
        String identityConfirmationMethod,
        String queueStatus,
        String priority,
        String actorId,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String CHANNEL_WALK_IN = "walk_in";
    public static final String CHANNEL_SCHEDULED = "scheduled";

    public static final String QUEUE_WAITING = "waiting";
    public static final String QUEUE_CALLED = "called";
    public static final String QUEUE_IN_ADMISSION = "in_admission";
    public static final String QUEUE_COMPLETED = "completed";
    public static final String QUEUE_ABANDONED = "abandoned";

    public static final String PRIORITY_NORMAL = "normal";
    public static final String PRIORITY_PRIORITY = "priority";
    public static final String PRIORITY_URGENT = "urgent";

    public static final String IDENTITY_DOCUMENT_CHECK = "document_check";
    public static final String IDENTITY_PORTAL_HANDOFF = "portal_handoff";
    public static final String IDENTITY_REPRESENTATIVE_VERIFICATION = "representative_verification";
}
