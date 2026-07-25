package com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityDecision;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersionRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

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

    @Test
    void versionWithNoDeclaredMetadataIsEvaluatedOnPlatformVersionOnly() {
        PackageVersionRepository repository = mock(PackageVersionRepository.class);
        when(repository.findByPackageIdAndVersion("pkg-1", CompatibilityEvaluator.PLATFORM_VERSION))
                .thenReturn(Optional.of(fixtureVersion(null)));
        CompatibilityEvaluator withCatalog = new CompatibilityEvaluator(repository);

        CompatibilityDecision decision = withCatalog.evaluate("pkg-1", CompatibilityEvaluator.PLATFORM_VERSION);
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE);
    }

    @Test
    void declaredApiContractVersionMajorMismatchIsIncompatible() {
        CompatibilityDecision decision = evaluateWithMetadata("apiContractVersion=2.0");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_INCOMPATIBLE);
    }

    @Test
    void declaredDatabaseSchemaVersionMinorMismatchIsCompatibleWithWarning() {
        CompatibilityDecision decision = evaluateWithMetadata("databaseSchemaVersion=1.1");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE_WITH_WARNING);
    }

    @Test
    void unrecognizedRequiredDependencyCapabilityIsUnknown() {
        CompatibilityDecision decision = evaluateWithMetadata("requiredCapabilities=BCM-PLT-999:1.0");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_UNKNOWN);
    }

    @Test
    void recognizedRequiredDependencyCapabilityWithMismatchedMajorIsIncompatible() {
        CompatibilityDecision decision = evaluateWithMetadata("requiredCapabilities=BCM-PLT-001:2.0");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_INCOMPATIBLE);
    }

    @Test
    void recognizedRequiredDependencyCapabilityWithMatchingMajorIsCompatible() {
        CompatibilityDecision decision = evaluateWithMetadata("requiredCapabilities=BCM-PLT-001:1.0");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE);
    }

    @Test
    void tenantRegionWithNoOverlapIsIncompatible() {
        CompatibilityDecision decision = evaluateWithMetadata("supportedRegions=BR");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_INCOMPATIBLE);
    }

    @Test
    void tenantRegionWithPartialOverlapIsCompatibleWithWarning() {
        CompatibilityDecision decision = evaluateWithMetadata("supportedRegions=MX,BR");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE_WITH_WARNING);
    }

    @Test
    void languageAndCurrencySupportFullyContainedIsCompatible() {
        CompatibilityDecision decision = evaluateWithMetadata("supportedLanguages=es-MX;supportedCurrencies=MXN");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE);
    }

    @Test
    void nonGenericRegulatoryProfileIsUnknown() {
        CompatibilityDecision decision = evaluateWithMetadata("regulatoryProfile=MX-NOM-006");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_UNKNOWN);
    }

    @Test
    void genericRegulatoryProfileIsCompatible() {
        CompatibilityDecision decision = evaluateWithMetadata("regulatoryProfile=GENERIC");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE);
    }

    @Test
    void requiredFeatureFlagsWithNoContextIsUnknown() {
        CompatibilityDecision decision = evaluateWithMetadata("requiredFeatureFlags=marketplace-beta");
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_UNKNOWN);
    }

    @Test
    void requiredFeatureFlagsAllEnabledIsCompatible() {
        PackageVersionRepository repository = mock(PackageVersionRepository.class);
        when(repository.findByPackageIdAndVersion("pkg-1", CompatibilityEvaluator.PLATFORM_VERSION))
                .thenReturn(Optional.of(fixtureVersion("requiredFeatureFlags=marketplace-beta")));
        CompatibilityEvaluator withCatalog = new CompatibilityEvaluator(repository);

        CompatibilityDecision decision = withCatalog.evaluate(
                "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, Set.of("marketplace-beta"));
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_COMPATIBLE);
    }

    @Test
    void requiredFeatureFlagsNotEnabledIsIncompatible() {
        PackageVersionRepository repository = mock(PackageVersionRepository.class);
        when(repository.findByPackageIdAndVersion("pkg-1", CompatibilityEvaluator.PLATFORM_VERSION))
                .thenReturn(Optional.of(fixtureVersion("requiredFeatureFlags=marketplace-beta")));
        CompatibilityEvaluator withCatalog = new CompatibilityEvaluator(repository);

        CompatibilityDecision decision = withCatalog.evaluate(
                "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, Set.of());
        assertThat(decision.decision()).isEqualTo(CompatibilityDecision.DECISION_INCOMPATIBLE);
    }

    private CompatibilityDecision evaluateWithMetadata(String metadataText) {
        PackageVersionRepository repository = mock(PackageVersionRepository.class);
        when(repository.findByPackageIdAndVersion("pkg-1", CompatibilityEvaluator.PLATFORM_VERSION))
                .thenReturn(Optional.of(fixtureVersion(metadataText)));
        return new CompatibilityEvaluator(repository).evaluate("pkg-1", CompatibilityEvaluator.PLATFORM_VERSION);
    }

    private PackageVersion fixtureVersion(String compatibilityMetadataText) {
        LocalDateTime now = LocalDateTime.now();
        return new PackageVersion("version-1", "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION,
                PackageVersion.STATUS_CERTIFIED, true, true, true, true, compatibilityMetadataText,
                new AuditMetadata("actor", now, "actor", now));
    }
}
