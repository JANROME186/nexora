package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

/**
 * Contact reachable in case of emergency, modeled as ENT-PAT-002 in the Patient Management
 * business model. Owned by the {@link Patient} aggregate.
 */
public record PatientEmergencyContact(
        String emergencyContactId,
        String patientId,
        String relationship,
        PersonName name,
        String phoneCountryCode,
        String phoneNationalNumber,
        boolean preferred) {
}
