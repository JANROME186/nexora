package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared;

/**
 * Structured personal name aligned with the Nexora Shared Kernel {@code VO-002 PersonName} and the
 * {@code VO-PER-002} value object of the Person Management business model. Names are never
 * concatenated at storage time; consumers format them locale-aware.
 */
public record PersonName(
        String givenName,
        String middleName,
        String familyName,
        String secondFamilyName,
        String preferredName) {

    public String fullNameDisplay() {
        StringBuilder builder = new StringBuilder();
        builder.append(givenName == null ? "" : givenName.trim());
        if (middleName != null && !middleName.isBlank()) {
            builder.append(' ').append(middleName.trim());
        }
        builder.append(' ').append(familyName == null ? "" : familyName.trim());
        if (secondFamilyName != null && !secondFamilyName.isBlank()) {
            builder.append(' ').append(secondFamilyName.trim());
        }
        return builder.toString().trim().replaceAll("\\s+", " ");
    }
}
