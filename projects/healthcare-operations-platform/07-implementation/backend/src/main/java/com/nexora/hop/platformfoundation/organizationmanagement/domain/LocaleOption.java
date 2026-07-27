package com.nexora.hop.platformfoundation.organizationmanagement.domain;

/** TD-DB-003: a read-only row of {@code organization.locales}. */
public record LocaleOption(String localeCode, String nameEsMx, String nameEnUs, boolean isDefault) {
}
