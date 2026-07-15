package com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.application;

public record StartReceptionVisitCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String patientId,
        String linkedAppointmentId,
        String intakeChannel,
        String actorId) {
}
