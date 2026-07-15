package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.application;

public record StartAdmissionRequestCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String visitId,
        String patientId,
        String doctorId,
        String actorId) {
}
