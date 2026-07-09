package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application;

import java.time.LocalDate;

/**
 * Register a new patient. Duplicate detection integration and match-resolution semantics
 * (BCM-PER-002 RN-002 and BCM-ATT-002 RN-006) are custom rules deferred to MVP-MOD-003-BE-002.
 */
public record RegisterPatientCommand(
        String tenantId,
        String laboratoryId,
        String patientCode,
        String givenName,
        String middleName,
        String familyName,
        String secondFamilyName,
        String preferredName,
        LocalDate birthDate,
        String sexAtBirth,
        String primaryDocumentType,
        String primaryDocumentNumber,
        String primaryDocumentIssuingCountry,
        LocalDate primaryDocumentIssuedAt,
        LocalDate primaryDocumentExpiresAt,
        String addressCountry,
        String addressState,
        String addressCity,
        String addressPostalCode,
        String addressStreet,
        String preferredLocale) {
}
