package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.application;

import java.time.LocalDate;
import java.util.List;

public record RequestAppointmentCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String patientId,
        String doctorId,
        LocalDate scheduledStart,
        LocalDate scheduledEnd,
        String channel,
        String actorId,
        List<RequestedItemInput> requestedItems) {

    public record RequestedItemInput(String testDefinitionId, String catalogItemKind) {
    }
}
