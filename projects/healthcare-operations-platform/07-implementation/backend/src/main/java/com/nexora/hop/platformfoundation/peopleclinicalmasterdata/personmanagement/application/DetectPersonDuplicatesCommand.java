package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application;

import java.time.LocalDate;

public record DetectPersonDuplicatesCommand(
        String tenantId,
        String personKind,
        String familyName,
        String givenName,
        LocalDate birthDate,
        String sexAtBirth,
        String nationalIdentifier) {
}
