package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.application;

import java.time.LocalDate;
import java.util.List;

/**
 * Anonymous public-website appointment intake command (BCM-ATT-001 RN-008, COM-MOD-011-BE-001).
 * Reuses the BCM-ATT-006 ProspectiveContact shape (fullName/phone/email inline). Never carries a
 * patientId; the resulting slot lands in {@link
 * com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain
 * .AppointmentSlot#STATUS_REQUESTED requested} state until staff confirmation.
 */
public record PublicRequestAppointmentCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String prospectiveFullName,
        String prospectivePhone,
        String prospectiveEmail,
        LocalDate scheduledStart,
        LocalDate scheduledEnd,
        List<RequestedItemInput> requestedItems) {

    public record RequestedItemInput(String testDefinitionId, String catalogItemKind) {
    }
}
