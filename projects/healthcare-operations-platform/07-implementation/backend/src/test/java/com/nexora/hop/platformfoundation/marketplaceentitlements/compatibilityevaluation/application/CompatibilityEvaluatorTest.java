package com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityDecision;

class CompatibilityEvaluatorTest {

    private final CompatibilityEvaluator evaluator = new CompatibilityEvaluator();

    @Test
    void sameMajorAndMinorVersionIsCompatible() {
        CompatibilityDecision decision = evaluator.evaluate(CompatibilityEvaluator.PLATFORM_VERSION);
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE);
        assertThat(decision.allowsInstallation()).isTrue();
        assertThat(decision.effect()).isEqualTo("allow_installation");
    }

    @Test
    void sameMajorDifferentMinorIsCompatibleWithWarning() {
        CompatibilityDecision decision = evaluator.evaluate("1.5.0");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE_WITH_WARNING);
        assertThat(decision.allowsInstallation()).isTrue();
    }

    @Test
    void differentMajorIsIncompatible() {
        CompatibilityDecision decision = evaluator.evaluate("2.0.0");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_INCOMPATIBLE);
        assertThat(decision.allowsInstallation()).isFalse();
        assertThat(decision.effect()).isEqualTo("block_installation");
    }

    @Test
    void blankVersionIsUnknown() {
        CompatibilityDecision decision = evaluator.evaluate(" ");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_UNKNOWN);
        assertThat(decision.allowsInstallation()).isFalse();
    }

    @Test
    void nonNumericVersionIsUnknown() {
        CompatibilityDecision decision = evaluator.evaluate("abc");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_UNKNOWN);
        assertThat(decision.effect()).isEqualTo("block_until_reviewed");
    }
}
