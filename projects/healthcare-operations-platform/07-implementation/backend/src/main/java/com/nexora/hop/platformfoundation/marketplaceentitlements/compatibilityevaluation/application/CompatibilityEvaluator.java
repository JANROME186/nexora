package com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityDecision;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityMetadata;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersionRepository;

/**
 * Compatibility evaluation strategy (compatibility.md). Evaluates every one of the 9 modeled
 * {@code compatibility_dimensions} (COM-MOD-017-BE-002, closing TD-BE-018): {@code
 * platform_version}, {@code api_contract_version}, {@code database_schema_version}, {@code
 * dependency_capability_versions}, {@code tenant_region}, {@code language_support}, {@code
 * currency_support}, {@code regulatory_profile} and {@code feature_flags}. Dimensions beyond
 * {@code platform_version} are only evaluated when the target {@link PackageVersion} declares
 * {@link CompatibilityMetadata}; an undeclared dimension is treated as compatible (never as a
 * claim of incompatibility), preserving COM-MOD-017-BE-001 behavior for versions with no declared
 * metadata.
 */
@Service
public class CompatibilityEvaluator {

    /** Current HOP platform version every marketplace package version is evaluated against. */
    public static final String PLATFORM_VERSION = "1.0.0";

    /** compatibility.md {@code required_dependency_capabilities}: shipped at the platform version, as core modules. */
    private static final Set<String> REQUIRED_DEPENDENCY_CAPABILITIES =
            Set.of("BCM-PLT-001", "BCM-PLT-002", "BCM-PLT-005", "BCM-PLT-006", "BCM-PLT-007", "BCM-PLT-009");

    private static final String PLATFORM_API_CONTRACT_VERSION = "1.0";
    private static final String PLATFORM_DATABASE_SCHEMA_VERSION = "1.0";
    private static final Set<String> PLATFORM_REGIONS = Set.of("MX", "US");
    private static final Set<String> PLATFORM_LANGUAGES = Set.of("es-MX", "en-US");
    private static final Set<String> PLATFORM_CURRENCIES = Set.of("MXN", "USD");
    private static final String REGULATORY_PROFILE_GENERIC = "GENERIC";

    private final PackageVersionRepository packageVersionRepository;

    @Autowired
    public CompatibilityEvaluator(PackageVersionRepository packageVersionRepository) {
        this.packageVersionRepository = packageVersionRepository;
    }

    /** No-catalog-lookup mode: evaluates {@code platform_version} only, as COM-MOD-017-BE-001 did. */
    public CompatibilityEvaluator() {
        this(null);
    }

    /** Evaluates {@code platform_version} only; kept for callers with no package context. */
    public CompatibilityDecision evaluate(String requestedVersion) {
        return evaluate(null, requestedVersion, null);
    }

    /** Evaluates every dimension except {@code feature_flags}, whose enabled state is unknown to this caller. */
    public CompatibilityDecision evaluate(String packageId, String requestedVersion) {
        return evaluate(packageId, requestedVersion, null);
    }

    /**
     * Evaluates all 9 compatibility.md dimensions. {@code enabledFeatureFlags} is the caller's
     * pre-resolved set of feature flags enabled for the requesting tenant (a policy-information-point
     * concern outside this evaluator's own module boundary); {@code null} means that information is
     * unavailable to the caller, resolving the {@code feature_flags} dimension to {@code unknown}
     * whenever the version declares required flags.
     */
    public CompatibilityDecision evaluate(String packageId, String requestedVersion, Set<String> enabledFeatureFlags) {
        CompatibilityDecision platformVersionDecision = evaluatePlatformVersion(requestedVersion);
        if (CompatibilityDecision.DECISION_INCOMPATIBLE.equals(platformVersionDecision.decision())
                || CompatibilityDecision.DECISION_UNKNOWN.equals(platformVersionDecision.decision())) {
            return platformVersionDecision;
        }
        if (packageVersionRepository == null || packageId == null) {
            return platformVersionDecision;
        }
        Optional<PackageVersion> versionRecord = packageVersionRepository.findByPackageIdAndVersion(packageId, requestedVersion);
        if (versionRecord.isEmpty() || versionRecord.get().compatibilityMetadataText() == null) {
            return platformVersionDecision;
        }
        CompatibilityMetadata metadata = CompatibilityMetadata.parse(versionRecord.get().compatibilityMetadataText());

        CompatibilityDecision worst = platformVersionDecision;
        worst = worseOf(worst, evaluateDeclaredVersion(
                "api_contract_version", metadata.apiContractVersion(), PLATFORM_API_CONTRACT_VERSION));
        worst = worseOf(worst, evaluateDeclaredVersion(
                "database_schema_version", metadata.databaseSchemaVersion(), PLATFORM_DATABASE_SCHEMA_VERSION));
        worst = worseOf(worst, evaluateDependencyCapabilities(metadata.requiredCapabilities()));
        worst = worseOf(worst, evaluateOverlap("tenant_region", metadata.supportedRegions(), PLATFORM_REGIONS));
        worst = worseOf(worst, evaluateOverlap("language_support", metadata.supportedLanguages(), PLATFORM_LANGUAGES));
        worst = worseOf(worst, evaluateOverlap("currency_support", metadata.supportedCurrencies(), PLATFORM_CURRENCIES));
        worst = worseOf(worst, evaluateRegulatoryProfile(metadata.regulatoryProfile()));
        worst = worseOf(worst, evaluateRequiredFeatureFlags(metadata.requiredFeatureFlags(), enabledFeatureFlags));
        return worst;
    }

    private CompatibilityDecision evaluatePlatformVersion(String requestedVersion) {
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

    private CompatibilityDecision evaluateDeclaredVersion(String dimension, String declared, String platformValue) {
        if (declared == null || declared.isBlank()) {
            return compatible();
        }
        String[] declaredParts = declared.split("\\.");
        String[] platformParts = platformValue.split("\\.");
        if (declaredParts.length < 1 || !isNumeric(declaredParts[0])) {
            return new CompatibilityDecision(CompatibilityDecision.DECISION_UNKNOWN, "block_until_reviewed",
                    "Declared " + dimension + " " + declared + " is not a recognizable version.");
        }
        if (!declaredParts[0].equals(platformParts[0])) {
            return new CompatibilityDecision(CompatibilityDecision.DECISION_INCOMPATIBLE, "block_installation",
                    "Declared " + dimension + " " + declared + " does not match platform " + dimension + " "
                            + platformValue + ".");
        }
        if (!declared.equals(platformValue)) {
            return new CompatibilityDecision(CompatibilityDecision.DECISION_COMPATIBLE_WITH_WARNING,
                    "allow_with_operator_acknowledgement",
                    "Declared " + dimension + " " + declared + " differs from platform " + dimension + " "
                            + platformValue + ".");
        }
        return compatible();
    }

    private CompatibilityDecision evaluateDependencyCapabilities(Map<String, String> requiredCapabilities) {
        if (requiredCapabilities.isEmpty()) {
            return compatible();
        }
        for (Map.Entry<String, String> required : requiredCapabilities.entrySet()) {
            if (!REQUIRED_DEPENDENCY_CAPABILITIES.contains(required.getKey())) {
                return new CompatibilityDecision(CompatibilityDecision.DECISION_UNKNOWN, "block_until_reviewed",
                        "Declared dependency capability " + required.getKey() + " is not a recognized platform capability.");
            }
            String[] declaredParts = required.getValue().split("\\.");
            if (declaredParts.length < 1 || !isNumeric(declaredParts[0])
                    || !declaredParts[0].equals(PLATFORM_VERSION.split("\\.")[0])) {
                return new CompatibilityDecision(CompatibilityDecision.DECISION_INCOMPATIBLE, "block_installation",
                        "Declared dependency capability " + required.getKey() + " version " + required.getValue()
                                + " is incompatible with the running platform.");
            }
        }
        return compatible();
    }

    private CompatibilityDecision evaluateOverlap(String dimension, Set<String> declared, Set<String> platformValues) {
        if (declared.isEmpty()) {
            return compatible();
        }
        boolean anyOverlap = declared.stream().anyMatch(platformValues::contains);
        boolean fullyContained = platformValues.containsAll(declared);
        if (!anyOverlap) {
            return new CompatibilityDecision(CompatibilityDecision.DECISION_INCOMPATIBLE, "block_installation",
                    "Declared " + dimension + " " + declared + " has no overlap with platform-supported " + platformValues + ".");
        }
        if (!fullyContained) {
            return new CompatibilityDecision(CompatibilityDecision.DECISION_COMPATIBLE_WITH_WARNING,
                    "allow_with_operator_acknowledgement",
                    "Declared " + dimension + " " + declared + " is only partially supported by the platform.");
        }
        return compatible();
    }

    private CompatibilityDecision evaluateRegulatoryProfile(String regulatoryProfile) {
        if (regulatoryProfile == null || regulatoryProfile.isBlank()
                || REGULATORY_PROFILE_GENERIC.equals(regulatoryProfile)) {
            return compatible();
        }
        return new CompatibilityDecision(CompatibilityDecision.DECISION_UNKNOWN, "block_until_reviewed",
                "Regulatory profile " + regulatoryProfile + " has no automated certification and requires operator review.");
    }

    private CompatibilityDecision evaluateRequiredFeatureFlags(
            Set<String> requiredFeatureFlags, Set<String> enabledFeatureFlags) {
        if (requiredFeatureFlags.isEmpty()) {
            return compatible();
        }
        if (enabledFeatureFlags == null) {
            return new CompatibilityDecision(CompatibilityDecision.DECISION_UNKNOWN, "block_until_reviewed",
                    "Required feature flags " + requiredFeatureFlags + " cannot be evaluated without feature-flag context.");
        }
        if (!enabledFeatureFlags.containsAll(requiredFeatureFlags)) {
            return new CompatibilityDecision(CompatibilityDecision.DECISION_INCOMPATIBLE, "block_installation",
                    "Required feature flags " + requiredFeatureFlags + " are not all enabled.");
        }
        return compatible();
    }

    private static CompatibilityDecision compatible() {
        return new CompatibilityDecision(CompatibilityDecision.DECISION_COMPATIBLE, "allow_installation", null);
    }

    /** compatibility.md decision severity: incompatible > unknown > compatible_with_warning > compatible. */
    private static CompatibilityDecision worseOf(CompatibilityDecision current, CompatibilityDecision candidate) {
        return severity(candidate.decision()) > severity(current.decision()) ? candidate : current;
    }

    private static int severity(String decision) {
        return switch (decision) {
            case CompatibilityDecision.DECISION_INCOMPATIBLE -> 3;
            case CompatibilityDecision.DECISION_UNKNOWN -> 2;
            case CompatibilityDecision.DECISION_COMPATIBLE_WITH_WARNING -> 1;
            default -> 0;
        };
    }

    private static boolean isNumeric(String value) {
        return value.chars().allMatch(Character::isDigit) && !value.isEmpty();
    }
}
