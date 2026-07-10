package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.application;

import java.time.LocalDate;
import java.util.List;

/**
 * Actor-supplied decision and data required to commit a {@code PatientRegistrationRequest}
 * (BCM-ATT-002 RN-001/RN-002/RN-003/RN-005/RN-006). The draft person data captured at
 * {@code startPatientRegistration} time (name, birth date, document) is reused from the stored
 * request; this command carries only what the intake actor decides or supplies at commit time:
 * match resolution, the remaining Patient aggregate fields, representative details when the
 * registration kind requires one, and the consents collected during intake.
 */
public record CommitPatientRegistrationCommand(
        String resolvedExistingPatientId,
        String patientCode,
        String sexAtBirth,
        String addressCountry,
        String addressState,
        String addressCity,
        String addressPostalCode,
        String addressStreet,
        String preferredLocale,
        String representativeRelationship,
        String representativeGivenName,
        String representativeMiddleName,
        String representativeFamilyName,
        String representativeSecondFamilyName,
        String representativeDocumentType,
        String representativeDocumentNumber,
        LocalDate representativeAuthorizationFrom,
        LocalDate representativeAuthorizationTo,
        List<ConsentSelection> consents) {

    public record ConsentSelection(String consentType, boolean granted, String grantedBy, String evidenceReference) {
    }
}
