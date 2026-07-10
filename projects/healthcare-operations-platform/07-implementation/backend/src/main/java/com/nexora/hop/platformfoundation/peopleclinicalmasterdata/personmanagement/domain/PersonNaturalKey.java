package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Locale;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;

/**
 * Normalized natural-key attributes used for duplicate detection ({@code VO-PER-001}). Fields are
 * normalized by {@link PeopleValidation#normalizeNaturalKeyToken(String)} before comparison to
 * satisfy BCM-PER-001 RN-001. {@code nationalIdentifierHash} is a one-way SHA-256 digest of the
 * normalized identifier (BCM-PER-001 RN-007): the raw national identifier value is never stored or
 * compared in clear text by the duplicate-detection engine, only the digest.
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
                hashIdentifier(nationalIdentifier));
    }

    /**
     * One-way SHA-256 hash of a normalized identifier value (national identifier or document
     * number), used exclusively for duplicate-detection comparison (BCM-PER-001 RN-007). Returns
     * {@code null} for a blank input so absence of an identifier never scores as a match.
     */
    public static String hashIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        String normalized = identifier.trim().toUpperCase(Locale.ROOT);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is a mandatory JDK algorithm (JLS/JCA baseline); this branch is unreachable
            // on any conforming JVM.
            throw new IllegalStateException("SHA-256 message digest is not available.", e);
        }
    }
}
