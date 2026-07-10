package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application;

/**
 * Tenant-configurable weighted scoring model for duplicate-person detection, implementing
 * BCM-PER-001 RN-003. The weights below are the Nexora-defined default baseline; a tenant may
 * override any of them through {@link TenantPeoplePolicyStore} without redeploying code. Weights
 * are expressed as fractions of a 0.0-1.0 confidence score and are expected to sum to 1.0 for the
 * default policy, though overrides are not required to sum exactly (the computed score is capped
 * to 0.99 either way so a single detector never reports absolute certainty).
 */
public record PersonDuplicateScoringPolicy(
        double familyNameWeight,
        double givenNameWeight,
        double birthDateWeight,
        double sexAtBirthWeight,
        double nationalIdentifierWeight,
        double highConfidenceThreshold) {

    public static final PersonDuplicateScoringPolicy DEFAULT = new PersonDuplicateScoringPolicy(
            0.35, 0.15, 0.25, 0.05, 0.20, 0.85);

    public PersonDuplicateScoringPolicy {
        if (highConfidenceThreshold <= 0 || highConfidenceThreshold > 1) {
            throw new IllegalArgumentException("highConfidenceThreshold must be in the (0,1] range.");
        }
    }
}
