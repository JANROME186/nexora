package com.nexora.hop.platformfoundation.organizationmanagement.domain;

/** TD-DB-003: a read-only row of {@code organization.currencies}. */
public record CurrencyOption(String currencyCode, String nameEsMx, String nameEnUs, int minorUnitDigits) {
}
