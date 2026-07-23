package com.nexora.hop.platformfoundation.platformconfiguration.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.ConfigParameter;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.FeatureFlag;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.PlatformConfigurationRepository;

class PlatformConfigurationServiceTest {

    private PlatformConfigurationRepository repository;
    private AuditRecorder auditRecorder;
    private PlatformConfigurationService service;

    @BeforeEach
    void setUp() {
        repository = mock(PlatformConfigurationRepository.class);
        auditRecorder = mock(AuditRecorder.class);
        service = new PlatformConfigurationService(repository, auditRecorder);
        when(repository.saveFeatureFlag(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void getConfigMasksEncryptedValuesButKeepsPlainValuesVisible() {
        when(repository.findAllConfigParameters()).thenReturn(List.of(
                new ConfigParameter("platform.security.session_timeout_minutes", "INTEGER", "30", true, false),
                new ConfigParameter("platform.security.api_secret", "STRING", "super-secret", false, true)));

        List<ConfigParameter> config = service.getConfig();

        assertThat(config).extracting(ConfigParameter::key)
                .containsExactly("platform.security.session_timeout_minutes", "platform.security.api_secret");
        assertThat(config.get(0).rawValue()).isEqualTo("30");
        assertThat(config.get(1).rawValue()).isEqualTo("***");
    }

    @Test
    void evaluateFeatureFlagsDefaultsToFalseWhenTenantIdIsMissing() {
        when(repository.findAllFeatureFlags()).thenReturn(List.of(
                new FeatureFlag("platform.experiments.new_dashboard", true, List.of(), 100, Instant.now(), "admin")));

        Map<String, Boolean> evaluated = service.evaluateFeatureFlags(null);

        assertThat(evaluated).containsEntry("platform.experiments.new_dashboard", false);
    }

    @Test
    void evaluateFeatureFlagsHonorsExplicitTenantTargeting() {
        when(repository.findAllFeatureFlags()).thenReturn(List.of(
                new FeatureFlag("platform.experiments.new_dashboard", false, List.of("tenant-1"), 0, Instant.now(), "admin")));

        assertThat(service.evaluateFeatureFlags("tenant-1"))
                .containsEntry("platform.experiments.new_dashboard", true);
        assertThat(service.evaluateFeatureFlags("tenant-2"))
                .containsEntry("platform.experiments.new_dashboard", false);
    }

    @Test
    void evaluateFeatureFlagsHonorsEnabledByDefault() {
        when(repository.findAllFeatureFlags()).thenReturn(List.of(
                new FeatureFlag("platform.experiments.always_on", true, List.of(), 0, Instant.now(), "admin")));

        assertThat(service.evaluateFeatureFlags("any-tenant"))
                .containsEntry("platform.experiments.always_on", true);
    }

    @Test
    void updateFeatureFlagPersistsAndRecordsAuditEvent() {
        FeatureFlag flag = service.updateFeatureFlag(new UpdateFeatureFlagCommand(
                "platform.experiments.new_dashboard", true, List.of("tenant-1"), 25, "admin-user"));

        assertThat(flag.flagKey()).isEqualTo("platform.experiments.new_dashboard");
        assertThat(flag.rolloutPercentage()).isEqualTo(25);
        verify(auditRecorder).recordSystemEvent(
                isNull(), eq("FeatureFlagUpdated"), eq("FeatureFlag"), eq(flag.flagKey()), anyString());
    }

    @Test
    void updateFeatureFlagRejectsKeyWithoutNamespaceFormatting() {
        assertThatThrownBy(() -> service.updateFeatureFlag(
                new UpdateFeatureFlagCommand("not-a-namespaced-key", true, List.of(), 0, "admin")))
                .isInstanceOf(InvalidPlatformConfigurationCommandException.class);
    }

    @Test
    void updateFeatureFlagRejectsRolloutPercentageOutOfRange() {
        assertThatThrownBy(() -> service.updateFeatureFlag(
                new UpdateFeatureFlagCommand("platform.experiments.x", true, List.of(), 150, "admin")))
                .isInstanceOf(InvalidPlatformConfigurationCommandException.class);
    }

    @Test
    void updateFeatureFlagRequiresUpdatedByActor() {
        assertThatThrownBy(() -> service.updateFeatureFlag(
                new UpdateFeatureFlagCommand("platform.experiments.x", true, List.of(), 0, " ")))
                .isInstanceOf(InvalidPlatformConfigurationCommandException.class);
    }
}
