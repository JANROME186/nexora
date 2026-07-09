package com.nexora.hop.platformfoundation.catalogtestconfiguration.shared;

import org.springframework.util.StringUtils;

/**
 * Shared field validation helpers reused by every catalog capability service, following the
 * same guard-clause style already used by organizationmanagement and identityaccess.
 */
public final class CatalogValidation {

    private CatalogValidation() {
    }

    public static String requiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidCatalogCommandException(message);
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
        throw new InvalidCatalogCommandException(message);
    }
}
