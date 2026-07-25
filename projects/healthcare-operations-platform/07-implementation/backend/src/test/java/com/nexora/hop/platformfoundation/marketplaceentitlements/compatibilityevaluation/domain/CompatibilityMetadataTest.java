package com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CompatibilityMetadataTest {

    @Test
    void nullTextParsesToEmpty() {
        CompatibilityMetadata metadata = CompatibilityMetadata.parse(null);
        assertThat(metadata).isEqualTo(CompatibilityMetadata.empty());
    }

    @Test
    void blankTextParsesToEmpty() {
        assertThat(CompatibilityMetadata.parse(" ")).isEqualTo(CompatibilityMetadata.empty());
    }

    @Test
    void parsesAllFields() {
        CompatibilityMetadata metadata = CompatibilityMetadata.parse(
                "apiContractVersion=1.0;databaseSchemaVersion=1.0;"
                        + "requiredCapabilities=BCM-PLT-001:1.0,BCM-PLT-005:1.0;"
                        + "supportedRegions=MX,US;supportedLanguages=es-MX,en-US;supportedCurrencies=MXN,USD;"
                        + "regulatoryProfile=GENERIC;requiredFeatureFlags=marketplace-beta");

        assertThat(metadata.apiContractVersion()).isEqualTo("1.0");
        assertThat(metadata.databaseSchemaVersion()).isEqualTo("1.0");
        assertThat(metadata.requiredCapabilities()).containsEntry("BCM-PLT-001", "1.0").containsEntry("BCM-PLT-005", "1.0");
        assertThat(metadata.supportedRegions()).containsExactlyInAnyOrder("MX", "US");
        assertThat(metadata.supportedLanguages()).containsExactlyInAnyOrder("es-MX", "en-US");
        assertThat(metadata.supportedCurrencies()).containsExactlyInAnyOrder("MXN", "USD");
        assertThat(metadata.regulatoryProfile()).isEqualTo("GENERIC");
        assertThat(metadata.requiredFeatureFlags()).containsExactly("marketplace-beta");
    }

    @Test
    void ignoresMalformedPairsAndUnknownKeys() {
        CompatibilityMetadata metadata = CompatibilityMetadata.parse("malformed;unknownKey=value;apiContractVersion=1.0");
        assertThat(metadata.apiContractVersion()).isEqualTo("1.0");
    }
}
