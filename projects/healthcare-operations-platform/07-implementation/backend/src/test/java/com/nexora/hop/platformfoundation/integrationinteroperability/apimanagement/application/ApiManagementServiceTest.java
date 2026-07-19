package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistration;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistrationRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKeyRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicy;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicyRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationConflictException;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationErrorCodes;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/** Unit coverage for RN-003's retirement transition and RN-004/RN-005's audited rate-limit policy (CUS-APIM). */
class ApiManagementServiceTest {

    private static final String OPERATION_ID = "getResultReports";

    private ApiSurfaceRegistrationRepository registrationRepository;
    private RateLimitPolicyRepository rateLimitPolicyRepository;
    private AuditRecorder auditRecorder;
    private AdjustableClock clock;
    private ApiManagementService service;

    @BeforeEach
    void setUp() {
        registrationRepository = mock(ApiSurfaceRegistrationRepository.class);
        PartnerApiKeyRepository partnerApiKeyRepository = mock(PartnerApiKeyRepository.class);
        rateLimitPolicyRepository = mock(RateLimitPolicyRepository.class);
        TenantDirectory tenantDirectory = mock(TenantDirectory.class);
        auditRecorder = mock(AuditRecorder.class);
        clock = new AdjustableClock(Instant.parse("2026-01-01T00:00:00Z"));
        service = new ApiManagementService(
                registrationRepository, partnerApiKeyRepository, rateLimitPolicyRepository, tenantDirectory,
                auditRecorder, clock);
        when(registrationRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(rateLimitPolicyRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(
                invocation -> invocation.getArgument(0));
    }

    @Test
    void retirementIsRejectedBeforeTheDeprecationWindowElapses() {
        LocalDateTime windowTo = LocalDateTime.now(clock).plusDays(1);
        ApiSurfaceRegistration scheduled = scheduledRegistration(windowTo);
        when(registrationRepository.findByOperationId(OPERATION_ID)).thenReturn(Optional.of(scheduled));

        IntegrationConflictException exception = assertThrows(IntegrationConflictException.class,
                () -> service.retireDeprecatedOperation(OPERATION_ID, "admin-1"));
        assertThat(exception.code()).isEqualTo(IntegrationErrorCodes.API_DEPRECATION_WINDOW_NOT_ELAPSED);
    }

    @Test
    void retirementSucceedsOnceTheDeprecationWindowHasElapsed() {
        LocalDateTime windowTo = LocalDateTime.now(clock).minusSeconds(1);
        ApiSurfaceRegistration scheduled = scheduledRegistration(windowTo);
        when(registrationRepository.findByOperationId(OPERATION_ID)).thenReturn(Optional.of(scheduled));

        ApiSurfaceRegistration retired = service.retireDeprecatedOperation(OPERATION_ID, "admin-1");

        assertThat(retired.deprecationStatus()).isEqualTo(ApiSurfaceRegistration.DEPRECATION_RETIRED);
        verify(auditRecorder).recordSystemEvent(
                anyString(), org.mockito.ArgumentMatchers.eq("ApiOperationRetired"), anyString(), anyString(),
                anyString());
    }

    @Test
    void retirementIsRejectedForAnOperationNotCurrentlyScheduled() {
        ApiSurfaceRegistration active = new ApiSurfaceRegistration(
                "reg-1", "tenant-1", "BCM-RES-002", OPERATION_ID, ApiSurfaceRegistration.CLASSIFICATION_PUBLIC,
                "v1", ApiSurfaceRegistration.DEPRECATION_ACTIVE, null, null, null, audit());
        when(registrationRepository.findByOperationId(OPERATION_ID)).thenReturn(Optional.of(active));

        IntegrationConflictException exception = assertThrows(IntegrationConflictException.class,
                () -> service.retireDeprecatedOperation(OPERATION_ID, "admin-1"));
        assertThat(exception.code()).isEqualTo(IntegrationErrorCodes.API_DEPRECATION_WINDOW_NOT_ELAPSED);
    }

    @Test
    void settingARateLimitPolicyRecordsAnAuditEvent() {
        when(rateLimitPolicyRepository.findByClassification("partner")).thenReturn(Optional.empty());

        RateLimitPolicy saved = service.setRateLimitPolicy("partner", 60, "admin-1");

        assertThat(saved.requestsPerMinute()).isEqualTo(60);
        verify(auditRecorder, times(1)).recordSystemEvent(
                org.mockito.ArgumentMatchers.eq("platform"),
                org.mockito.ArgumentMatchers.eq("RateLimitPolicySet"),
                anyString(), anyString(), anyString());
    }

    private ApiSurfaceRegistration scheduledRegistration(LocalDateTime windowTo) {
        return new ApiSurfaceRegistration(
                "reg-1", "tenant-1", "BCM-RES-002", OPERATION_ID, ApiSurfaceRegistration.CLASSIFICATION_PARTNER,
                "v1", ApiSurfaceRegistration.DEPRECATION_SCHEDULED, LocalDateTime.now(clock).minusDays(1), windowTo,
                "Use v2 instead.", audit());
    }

    private AuditMetadata audit() {
        return new AuditMetadata("admin-1", LocalDateTime.now(clock), "admin-1", LocalDateTime.now(clock));
    }

    /** Minimal mutable {@link Clock} letting tests deterministically fast-forward time. */
    private static final class AdjustableClock extends Clock {
        private final Instant instant;

        AdjustableClock(Instant instant) {
            this.instant = instant;
        }

        @Override
        public java.time.ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
