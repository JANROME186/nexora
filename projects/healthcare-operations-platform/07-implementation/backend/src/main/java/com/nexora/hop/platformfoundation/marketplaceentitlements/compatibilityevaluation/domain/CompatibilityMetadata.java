package com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parses a {@code PackageVersion.compatibilityMetadataText} delimited declaration into the
 * remaining compatibility.md dimensions beyond {@code platform_version}: {@code
 * api_contract_version}, {@code database_schema_version}, {@code dependency_capability_versions},
 * {@code tenant_region}, {@code language_support}, {@code currency_support}, {@code
 * regulatory_profile} and {@code feature_flags} (COM-MOD-017-BE-002).
 *
 * <p>Format is {@code key=value} pairs separated by {@code ;}, mirroring the {@code
 * capabilityMappingsText}/{@code tierCodesText} delimited-text convention already used elsewhere
 * in this capability package rather than introducing a JSON column. Multi-value fields use a
 * {@code ,}-separated value, e.g. {@code requiredCapabilities=BCM-PLT-001:1.0,BCM-PLT-005:1.0}. An
 * absent key means that dimension was not declared and is skipped by {@code CompatibilityEvaluator}
 * (treated as compatible on that axis, never as a claim of incompatibility).
 */
public record CompatibilityMetadata(
        String apiContractVersion,
        String databaseSchemaVersion,
        Map<String, String> requiredCapabilities,
        Set<String> supportedRegions,
        Set<String> supportedLanguages,
        Set<String> supportedCurrencies,
        String regulatoryProfile,
        Set<String> requiredFeatureFlags) {

    public static CompatibilityMetadata empty() {
        return new CompatibilityMetadata(null, null, Map.of(), Set.of(), Set.of(), Set.of(), null, Set.of());
    }

    public static CompatibilityMetadata parse(String text) {
        if (text == null || text.isBlank()) {
            return empty();
        }
        Map<String, String> fields = new LinkedHashMap<>();
        for (String pair : text.split(";")) {
            int separator = pair.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            fields.put(pair.substring(0, separator).trim(), pair.substring(separator + 1).trim());
        }
        return new CompatibilityMetadata(
                fields.get("apiContractVersion"),
                fields.get("databaseSchemaVersion"),
                parseCapabilityVersions(fields.get("requiredCapabilities")),
                parseSet(fields.get("supportedRegions")),
                parseSet(fields.get("supportedLanguages")),
                parseSet(fields.get("supportedCurrencies")),
                fields.get("regulatoryProfile"),
                parseSet(fields.get("requiredFeatureFlags")));
    }

    private static Set<String> parseSet(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        Set<String> values = new LinkedHashSet<>();
        for (String item : value.split(",")) {
            if (!item.isBlank()) {
                values.add(item.trim());
            }
        }
        return values;
    }

    private static Map<String, String> parseCapabilityVersions(String value) {
        if (value == null || value.isBlank()) {
            return Map.of();
        }
        Map<String, String> capabilities = new LinkedHashMap<>();
        for (String entry : value.split(",")) {
            int separator = entry.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            capabilities.put(entry.substring(0, separator).trim(), entry.substring(separator + 1).trim());
        }
        return capabilities;
    }
}
