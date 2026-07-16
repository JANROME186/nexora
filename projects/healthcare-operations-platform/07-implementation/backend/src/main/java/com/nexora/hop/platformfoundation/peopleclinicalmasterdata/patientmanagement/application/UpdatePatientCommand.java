package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application;

import java.time.LocalDate;

/**
 * Update the mutable fields of an existing patient. Terminal statuses (deceased) reject updates
 * per BCM-PER-002 RN-009.
 */
public record UpdatePatientCommand(
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
        String preferredLocale) implements PatientProfileFields {
}
