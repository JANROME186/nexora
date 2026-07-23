package com.nexora.hop.platformfoundation.platformconfiguration.application;

import java.util.List;

/** BCM-PLT-002 {@code updateFeatureFlag} command. */
public record UpdateFeatureFlagCommand(
        String flagKey,
        boolean enabledByDefault,
        List<String> targetTenants,
        Integer rolloutPercentage,
        String updatedBy) {
}
