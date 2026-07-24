package com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain;

/**
 * Outcome of a package/platform compatibility evaluation (compatibility.yaml {@code decisions}).
 * {@link #effect()} mirrors the modeled {@code allow_installation}/{@code
 * allow_with_operator_acknowledgement}/{@code block_installation}/{@code block_until_reviewed}
 * effects.
 */
public record CompatibilityDecision(String decision, String effect, String reason) {

    public static final String DECISION_COMPATIBLE = "compatible";
    public static final String DECISION_COMPATIBLE_WITH_WARNING = "compatible_with_warning";
    public static final String DECISION_INCOMPATIBLE = "incompatible";
    public static final String DECISION_UNKNOWN = "unknown";

    public boolean allowsInstallation() {
        return DECISION_COMPATIBLE.equals(decision) || DECISION_COMPATIBLE_WITH_WARNING.equals(decision);
    }
}
