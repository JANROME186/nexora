package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application;

import java.time.LocalDate;

public record SearchPersonsQuery(
        String tenantId,
        String personKind,
        String familyName,
        String givenName,
        LocalDate birthDate) {
}
