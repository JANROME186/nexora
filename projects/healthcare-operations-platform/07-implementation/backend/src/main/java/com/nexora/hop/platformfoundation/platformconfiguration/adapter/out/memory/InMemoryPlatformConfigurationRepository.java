package com.nexora.hop.platformfoundation.platformconfiguration.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.platformconfiguration.domain.ConfigParameter;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.FeatureFlag;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.PlatformConfigurationRepository;

@Repository
@Profile("!local")
class InMemoryPlatformConfigurationRepository implements PlatformConfigurationRepository {

    private final Map<String, ConfigParameter> configParameters = new ConcurrentHashMap<>();
    private final Map<String, FeatureFlag> featureFlags = new ConcurrentHashMap<>();

    InMemoryPlatformConfigurationRepository() {
        configParameters.put("platform.security.session_timeout_minutes", new ConfigParameter(
                "platform.security.session_timeout_minutes", "INTEGER", "30", true, false));
        configParameters.put("platform.operations.maintenance_mode", new ConfigParameter(
                "platform.operations.maintenance_mode", "BOOLEAN", "false", false, false));
    }

    @Override
    public List<ConfigParameter> findAllConfigParameters() {
        return configParameters.values().stream().sorted((left, right) -> left.key().compareTo(right.key())).toList();
    }

    @Override
    public List<FeatureFlag> findAllFeatureFlags() {
        return featureFlags.values().stream().sorted((left, right) -> left.flagKey().compareTo(right.flagKey())).toList();
    }

    @Override
    public Optional<FeatureFlag> findFeatureFlagByKey(String flagKey) {
        return Optional.ofNullable(featureFlags.get(flagKey));
    }

    @Override
    public FeatureFlag saveFeatureFlag(FeatureFlag flag) {
        featureFlags.put(flag.flagKey(), flag);
        return flag;
    }
}
