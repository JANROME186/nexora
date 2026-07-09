package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared;

/**
 * Residential or professional address. Aligned with the Nexora Shared Kernel {@code VO-005 Address}
 * and reused as {@code VO-PER-005} across the People capability packages.
 */
public record PersonAddress(
        String country,
        String state,
        String city,
        String municipality,
        String postalCode,
        String street,
        String externalNumber,
        String internalNumber) {
}
