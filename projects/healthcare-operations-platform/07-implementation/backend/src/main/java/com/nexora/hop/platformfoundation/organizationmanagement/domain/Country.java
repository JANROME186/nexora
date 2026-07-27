package com.nexora.hop.platformfoundation.organizationmanagement.domain;

/** TD-DB-003: a read-only row of {@code organization.countries}. */
public record Country(String countryCode, String nameEsMx, String nameEnUs, String status) {
}
