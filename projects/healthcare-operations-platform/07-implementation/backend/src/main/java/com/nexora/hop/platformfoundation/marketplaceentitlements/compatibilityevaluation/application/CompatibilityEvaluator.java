package com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application;

import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityDecision;

/**
 * Stateless compatibility evaluation strategy (compatibility.yaml). Compiles the generatable
 * {@code evaluateCompatibility} output with a basic {@code platform_version} major/minor
 * comparison; the full multi-dimension strategy (api_contract_version, database_schema_version,
 * dependency_capability_versions, tenant_region, language/currency support, regulatory_profile,
 * feature_flags) is the explicit custom_implementation_point deferred to a future BE-002
 * (TD-BE-018), per generation-plan.yaml.
 */
@Service
public class CompatibilityEvaluator {

    /** Current HOP platform version every marketplace package version is evaluated against. */
    public static final String PLATFORM_VERSION = "1.0.0";

    public CompatibilityDecision evaluate(String requestedVersion) {
        if (requestedVersion == null || requestedVersion.isBlank()) {
            return new CompatibilityDecision(
                    CompatibilityDecision.DECISION_UNKNOWN, "block_until_reviewed", "No version was supplied.");
        }
        String[] requested = requestedVersion.split("\\.");
        String[] platform = PLATFORM_VERSION.split("\\.");
        if (requested.length < 2 || !isNumeric(requested[0]) || !isNumeric(requested[1])) {
            return new CompatibilityDecision(
                    CompatibilityDecision.DECISION_UNKNOWN, "block_until_reviewed",
                    "Version " + requestedVersion + " is not a recognizable semantic version.");
        }
        if (!requested[0].equals(platform[0])) {
            return new CompatibilityDecision(
                    CompatibilityDecision.DECISION_INCOMPATIBLE, "block_installation",
                    "Major version " + requested[0] + " does not match platform major version " + platform[0] + ".");
        }
        if (!requested[1].equals(platform[1])) {
            return new CompatibilityDecision(
                    CompatibilityDecision.DECISION_COMPATIBLE_WITH_WARNING, "allow_with_operator_acknowledgement",
                    "Minor version " + requested[1] + " differs from platform minor version " + platform[1] + ".");
        }
        return new CompatibilityDecision(CompatibilityDecision.DECISION_COMPATIBLE, "allow_installation", null);
    }

    private static boolean isNumeric(String value) {
        return value.chars().allMatch(Character::isDigit) && !value.isEmpty();
    }
}
