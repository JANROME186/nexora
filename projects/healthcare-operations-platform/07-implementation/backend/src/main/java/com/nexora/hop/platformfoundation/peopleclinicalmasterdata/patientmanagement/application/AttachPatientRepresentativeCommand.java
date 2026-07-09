package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application;

import java.time.LocalDate;

public record AttachPatientRepresentativeCommand(
        String relationship,
        String givenName,
        String middleName,
        String familyName,
        String secondFamilyName,
        String documentType,
        String documentNumber,
        LocalDate authorizationFrom,
        LocalDate authorizationTo) {
}
