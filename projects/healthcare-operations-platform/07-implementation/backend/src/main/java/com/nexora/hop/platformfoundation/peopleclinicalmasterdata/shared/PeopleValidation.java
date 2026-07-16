package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared;

import java.text.Normalizer;
import java.util.Locale;

import org.springframework.util.StringUtils;

/**
 * Shared field-level validation helpers used by every People and Clinical Master Data service.
 * Mirrors the {@code CatalogValidation} style of the catalog-test-configuration module.
 * <p>
 * The {@link #normalizeNaturalKeyToken(String)} helper implements the generatable normalization
 * requirement from BCM-PER-001 RN-001 (natural keys must be uppercased and stripped of diacritics
 * before hashing or comparison). The tenant-configurable weighted confidence scoring described in
 * BCM-PER-001 RN-003 stays out of scope and is deferred to MVP-MOD-003-BE-002.
 */
public final class PeopleValidation {

    private PeopleValidation() {
    }

    public static String requiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidPeopleCommandException(message);
        }
        return value.trim();
    }

    public static String optionalText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    public static String requiredOneOf(String value, String message, String... allowedValues) {
        String trimmed = requiredText(value, message);
        for (String allowed : allowedValues) {
            if (allowed.equals(trimmed)) {
                return trimmed;
            }
        }
        throw new InvalidPeopleCommandException(message);
    }

    public static String optionalOneOf(String value, String message, String... allowedValues) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return requiredOneOf(value, message, allowedValues);
    }

    /**
     * Normalizes a natural-key token by trimming, upper-casing and stripping diacritics. Applies
     * BCM-PER-001 RN-001. Returns {@code null} for {@code null} or blank input.
     */
    public static String normalizeNaturalKeyToken(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFKC);
        String uppercased = normalized.toUpperCase(Locale.ROOT);
        String decomposed = Normalizer.normalize(uppercased, Normalizer.Form.NFD);
        String stripped = decomposed
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return Normalizer.normalize(stripped, Normalizer.Form.NFC);
    }
}
