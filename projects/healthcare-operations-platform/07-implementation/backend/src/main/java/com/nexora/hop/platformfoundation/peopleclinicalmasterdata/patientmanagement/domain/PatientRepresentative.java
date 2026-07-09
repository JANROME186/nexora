package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain;

import java.time.LocalDate;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

/**
 * Authorized representative that may act on behalf of the patient. Modeled as ENT-PAT-003 and
 * owned by the {@link Patient} aggregate. Enforcement of the active authorization window (BCM-
 * PER-002 RN-006) is deferred to MVP-MOD-003-BE-002.
 */
public record PatientRepresentative(
        String representativeId,
        String patientId,
        String relationship,
        PersonName representativeName,
        PersonDocument representativeDocument,
        LocalDate authorizationFrom,
        LocalDate authorizationTo,
        String status) {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_REVOKED = "revoked";
    public static final String STATUS_EXPIRED = "expired";
}
