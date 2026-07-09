package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application;

public record RecordPatientConsentCommand(
        String consentType,
        boolean granted,
        String grantedBy,
        String evidenceReference) {
}
