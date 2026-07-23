package com.nexora.hop.platformfoundation.platformconfiguration.domain;

import java.util.List;
import java.util.Optional;

public interface PlatformConfigurationRepository {

    List<ConfigParameter> findAllConfigParameters();

    List<FeatureFlag> findAllFeatureFlags();

    Optional<FeatureFlag> findFeatureFlagByKey(String flagKey);

    FeatureFlag saveFeatureFlag(FeatureFlag flag);
}
