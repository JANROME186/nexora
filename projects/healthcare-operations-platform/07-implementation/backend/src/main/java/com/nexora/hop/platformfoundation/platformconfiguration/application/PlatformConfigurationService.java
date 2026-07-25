package com.nexora.hop.platformfoundation.platformconfiguration.application;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.ConfigParameter;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.FeatureFlag;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.PlatformConfigurationRepository;

/**
 * BCM-PLT-002 Platform Configuration and Feature Flags. Compiled by COM-MOD-012-BE-001 to close
 * the "feature flags / operational configuration" gap tracked by the OPS-002 runbooks.
 */
@Service
public class PlatformConfigurationService {

    /** business-model.md invariant: "Config keys must follow standard namespace formatting". */
    private static final Pattern NAMESPACE_KEY = Pattern.compile("^[a-z][a-z0-9]*(\\.[a-z0-9_]+)+$");
    private static final int MIN_ROLLOUT_PERCENTAGE = 0;
    private static final int MAX_ROLLOUT_PERCENTAGE = 100;
    private static final String MASKED_VALUE = "***";

    private final PlatformConfigurationRepository repository;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PlatformConfigurationService(PlatformConfigurationRepository repository, AuditRecorder auditRecorder) {
        this(repository, auditRecorder, Clock.systemUTC());
    }

    private PlatformConfigurationService(
            PlatformConfigurationRepository repository, AuditRecorder auditRecorder, Clock clock) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** BCM-PLT-002 {@code getPlatformConfig}: masks encrypted values before returning them. */
    public List<ConfigParameter> getConfig() {
        return repository.findAllConfigParameters().stream().map(PlatformConfigurationService::maskIfEncrypted).toList();
    }

    /**
     * BCM-PLT-002 {@code evaluateFeatureFlags}: deterministic per-tenant evaluation of every known
     * flag. business-model.md invariant: "Feature flag evaluation must default to false if
     * targeting rules fail" — an evaluation is never allowed to throw; missing tenant context or an
     * unmatched targeting rule both resolve to {@code false}.
     */
    public Map<String, Boolean> evaluateFeatureFlags(String tenantId) {
        Map<String, Boolean> evaluated = new LinkedHashMap<>();
        for (FeatureFlag flag : repository.findAllFeatureFlags()) {
            evaluated.put(flag.flagKey(), evaluateOne(flag, tenantId));
        }
        return evaluated;
    }

    /** BCM-PLT-002 {@code updateFeatureFlag}: a privileged, audited operational configuration change. */
    public FeatureFlag updateFeatureFlag(UpdateFeatureFlagCommand command) {
        String flagKey = requiredFlagKey(command.flagKey());
        int rollout = requiredRolloutPercentage(command.rolloutPercentage());
        String updatedBy = requiredText(command.updatedBy(), "Updated-by actor id is required.");
        List<String> targetTenants = command.targetTenants() == null ? List.of() : List.copyOf(command.targetTenants());

        FeatureFlag flag = repository.saveFeatureFlag(new FeatureFlag(
                flagKey, command.enabledByDefault(), targetTenants, rollout, Instant.now(clock), updatedBy));

        auditRecorder.recordSystemEvent(null, "FeatureFlagUpdated", "FeatureFlag", flag.flagKey(),
                "{\"enabledByDefault\":%s,\"rolloutPercentage\":%d,\"updatedBy\":\"%s\"}"
                        .formatted(flag.enabledByDefault(), flag.rolloutPercentage(), jsonText(updatedBy)));
        return flag;
    }

    private boolean evaluateOne(FeatureFlag flag, String tenantId) {
        if (!StringUtils.hasText(tenantId)) {
            return false;
        }
        if (flag.targetTenants().contains(tenantId)) {
            return true;
        }
        if (flag.enabledByDefault()) {
            return true;
        }
        if (flag.rolloutPercentage() <= MIN_ROLLOUT_PERCENTAGE) {
            return false;
        }
        int bucket = Math.floorMod((tenantId + "|" + flag.flagKey()).hashCode(), MAX_ROLLOUT_PERCENTAGE);
        return bucket < flag.rolloutPercentage();
    }

    private static ConfigParameter maskIfEncrypted(ConfigParameter parameter) {
        if (!parameter.isEncrypted()) {
            return parameter;
        }
        return new ConfigParameter(
                parameter.key(), parameter.valueType(), MASKED_VALUE, parameter.tenantOverrideAllowed(), true);
    }

    private static String requiredFlagKey(String value) {
        String flagKey = requiredText(value, "Feature flag key is required.");
        if (!NAMESPACE_KEY.matcher(flagKey).matches()) {
            throw new InvalidPlatformConfigurationCommandException(
                    "Feature flag key must follow namespace formatting, e.g. platform.security.mfa_enforced.");
        }
        return flagKey;
    }

    private static int requiredRolloutPercentage(Integer value) {
        int rollout = value == null ? MIN_ROLLOUT_PERCENTAGE : value;
        if (rollout < MIN_ROLLOUT_PERCENTAGE || rollout > MAX_ROLLOUT_PERCENTAGE) {
            throw new InvalidPlatformConfigurationCommandException("Rollout percentage must be between 0 and 100.");
        }
        return rollout;
    }

    private static String requiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidPlatformConfigurationCommandException(message);
        }
        return value.trim();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
