package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain;

import java.time.LocalDate;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;

/**
 * Normalized natural-key attributes used for duplicate detection ({@code VO-PER-001}). Fields are
 * normalized by {@link PeopleValidation#normalizeNaturalKeyToken(String)} before comparison to
 * satisfy BCM-PER-001 RN-001. Advanced hashing of {@code nationalIdentifierHash} is deferred to
 * MVP-MOD-003-BE-002 (BCM-PER-001 RN-007).
 */
public record PersonNaturalKey(
        String normalizedFamilyName,
        String normalizedGivenName,
        LocalDate birthDate,
        String sexAtBirth,
        String nationalIdentifierHash) {

    public static PersonNaturalKey normalize(String familyName, String givenName, LocalDate birthDate,
            String sexAtBirth, String nationalIdentifier) {
        return new PersonNaturalKey(
                PeopleValidation.normalizeNaturalKeyToken(familyName),
                PeopleValidation.normalizeNaturalKeyToken(givenName),
                birthDate,
                sexAtBirth,
                nationalIdentifier == null || nationalIdentifier.isBlank()
                        ? null
                        // MVP compilation uses a stable non-cryptographic scrambling. A one-way
                        // cryptographic hash is delivered as part of MVP-MOD-003-BE-002 (RN-007).
                        : Integer.toHexString(nationalIdentifier.trim().hashCode()));
    }
}
