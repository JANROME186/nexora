package com.nexora.hop.platformfoundation.platformconfiguration.domain;

import java.time.Instant;
import java.util.List;

/** BCM-PLT-002 {@code FeatureFlag} entity (business-model.yaml). */
public record FeatureFlag(
        String flagKey,
        boolean enabledByDefault,
        List<String> targetTenants,
        int rolloutPercentage,
        Instant updatedAt,
        String updatedBy) {

    public FeatureFlag {
        targetTenants = targetTenants == null ? List.of() : List.copyOf(targetTenants);
    }
}
