package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.application;

import java.time.LocalDate;

public record StartPatientRegistrationCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String intakeChannel,
        String registrationKind,
        String givenName,
        String familyName,
        LocalDate birthDate,
        String documentType,
        String documentNumber,
        String draftPatientCode,
        String actorId) {
}
