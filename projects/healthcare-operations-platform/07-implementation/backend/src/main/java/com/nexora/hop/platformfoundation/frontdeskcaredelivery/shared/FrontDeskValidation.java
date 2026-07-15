package com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared;

import org.springframework.util.StringUtils;

/**
 * Shared field-level validation helpers used by every Front Desk and Care Delivery service.
 * Mirrors the {@code PeopleValidation} / {@code CatalogValidation} style of the earlier modules.
 */
public final class FrontDeskValidation {

    private FrontDeskValidation() {
    }

    public static String requiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidFrontDeskCommandException(message);
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
        throw new InvalidFrontDeskCommandException(message);
    }

    public static String optionalOneOf(String value, String message, String... allowedValues) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return requiredOneOf(value, message, allowedValues);
    }

    public static int requiredPositiveInt(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new InvalidFrontDeskCommandException(message);
        }
        return value;
    }
}
