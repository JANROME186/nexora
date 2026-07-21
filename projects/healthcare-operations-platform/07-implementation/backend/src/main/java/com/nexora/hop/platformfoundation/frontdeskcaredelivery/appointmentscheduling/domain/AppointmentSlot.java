package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Appointment slot process record (ENT-APT-001) owned by this capability. It does not mutate the
 * DiagnosticOrder aggregate directly (AGG-007, owned by BCM-LAB-001); on check-in it hands off to
 * Reception Management, which eventually leads to order creation through
 * {@code DiagnosticOrderManagementService}.
 * <p>
 * COM-MOD-011-BE-001 extension: {@code prospectiveFullName/Phone/Email} carry the {@code
 * ProspectiveContact} shape reused from BCM-ATT-006 for anonymous public-website appointment
 * requests (RN-008). {@code patientId} is nullable for those requests only; every non-public
 * channel continues to require a registered patient. No new aggregate or capability package was
 * introduced.
 */
public record AppointmentSlot(
        String appointmentId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String patientId,
        String doctorId,
        LocalDate scheduledStart,
        LocalDate scheduledEnd,
        String channel,
        String status,
        String linkedOrderId,
        String cancellationReason,
        String actorId,
        String prospectiveFullName,
        String prospectivePhone,
        String prospectiveEmail,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String CHANNEL_WALK_IN_SCHEDULING = "walk_in_scheduling";
    public static final String CHANNEL_PHONE = "phone";
    public static final String CHANNEL_EMPLOYEE_PORTAL = "employee_portal";
    public static final String CHANNEL_PATIENT_PORTAL_REQUEST_LATER = "patient_portal_request_later";
    public static final String CHANNEL_PUBLIC_WEBSITE = "public_website";

    public static final String STATUS_REQUESTED = "requested";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_CHECKED_IN = "checked_in";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_NO_SHOW = "no_show";
    public static final String STATUS_COMPLETED = "completed";
}
