package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.ExternalMessageEnvelope;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAcknowledgement;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAdapterException;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAdapterPort;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpoint;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpointRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecord;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecordRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationConflictException;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationErrorCodes;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/** Unit coverage for the RN-004/RN-005 bounded retry/dead-letter/correlation-id rules (CUS-INT-004-04/05). */
class IntegrationManagementServiceTest {

    private static final String ENDPOINT_ID = "ep-1";
    private static final String MESSAGE_ID = "m-1";

    private IntegrationEndpointRepository endpointRepository;
    private IntegrationMessageRecordRepository messageRepository;
    private IntegrationAdapterPort adapterPort;
    private AdjustableClock clock;
    private IntegrationManagementService service;
    private IntegrationEndpoint endpoint;

    @BeforeEach
    void setUp() {
        endpointRepository = mock(IntegrationEndpointRepository.class);
        messageRepository = mock(IntegrationMessageRecordRepository.class);
        adapterPort = mock(IntegrationAdapterPort.class);
        TenantDirectory tenantDirectory = mock(TenantDirectory.class);
        AuditRecorder auditRecorder = mock(AuditRecorder.class);
        clock = new AdjustableClock(Instant.parse("2026-01-01T00:00:00Z"));
        service = new IntegrationManagementService(
                endpointRepository, messageRepository, adapterPort, tenantDirectory, auditRecorder, clock);

        AuditMetadata audit = new AuditMetadata("integrator-1", LocalDateTime.now(clock), "integrator-1",
                LocalDateTime.now(clock));
        endpoint = new IntegrationEndpoint(
                ENDPOINT_ID, "tenant-1", "lab-1", "LIS Feed", IntegrationEndpoint.PROTOCOL_HL7V2,
                IntegrationEndpoint.DIRECTION_INBOUND, IntegrationEndpoint.STATUS_ACTIVE, audit);
        when(endpointRepository.findById(ENDPOINT_ID)).thenReturn(Optional.of(endpoint));
        // messageRepository.save simply echoes what it was given, mirroring the in-memory adapter.
        when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void correlationIdIsDeterministicAndStableRegardlessOfDerivationOrder() {
        String first = IntegrationManagementService.deriveCorrelationId(ENDPOINT_ID, "EXT-1");
        String second = IntegrationManagementService.deriveCorrelationId(ENDPOINT_ID, "EXT-1");
        String different = IntegrationManagementService.deriveCorrelationId(ENDPOINT_ID, "EXT-2");
        assertThat(first).isEqualTo(second);
        assertThat(first).isNotEqualTo(different);
    }

    @Test
    void retryBeforeNextRetryAtIsRejected() {
        IntegrationMessageRecord failing = failedRecord(0, clock.instant().plusSeconds(30));
        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(failing));

        IntegrationConflictException exception = assertThrows(IntegrationConflictException.class,
                () -> service.retryMessage(MESSAGE_ID, "x=1", "integrator-1"));
        assertThat(exception.code()).isEqualTo(IntegrationErrorCodes.INTEGRATION_RETRY_NOT_YET_DUE);
    }

    @Test
    void retryAfterBackoffElapsesReschedulesWithAnIncreasedBackoffOnRepeatedFailure() throws Exception {
        IntegrationMessageRecord failing = failedRecord(0, clock.instant());
        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(failing));
        when(adapterPort.normalizeMessage(any(), anyString()))
                .thenThrow(new IntegrationAdapterException("still invalid", "INTEGRATION_NORMALIZATION_FAILED"));

        clock.advanceSeconds(31);
        IntegrationMessageRecord retried = service.retryMessage(MESSAGE_ID, "still INVALID", "integrator-1");

        assertThat(retried.normalizationStatus()).isEqualTo(IntegrationMessageRecord.STATUS_RETRYING);
        assertThat(retried.retryCount()).isEqualTo(1);
        assertThat(retried.correlationId()).isEqualTo(failing.correlationId());
        assertThat(retried.nextRetryAt()).isAfter(clock.instant().atZone(ZoneOffset.UTC).toLocalDateTime());
        assertThat(retried.isReadyForDownstreamRouting()).isFalse();
    }

    @Test
    void repeatedFailuresEventuallyDeadLetterTheMessageAndBlockFurtherRetries() {
        when(adapterPort.normalizeMessage(any(), anyString()))
                .thenThrow(new IntegrationAdapterException("still invalid", "INTEGRATION_NORMALIZATION_FAILED"));

        IntegrationMessageRecord current = failedRecord(0, clock.instant());
        String correlationId = current.correlationId();
        while (!IntegrationMessageRecord.STATUS_DEAD_LETTERED.equals(current.normalizationStatus())) {
            when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(current));
            clock.advanceSeconds(3600);
            current = service.retryMessage(MESSAGE_ID, "still INVALID", "integrator-1");
        }

        assertThat(current.retryCount()).isEqualTo(IntegrationManagementService.MAX_RETRY_COUNT);
        assertThat(current.deadLetterReason()).isNotBlank();
        assertThat(current.correlationId()).isEqualTo(correlationId);
        assertThat(current.isReadyForDownstreamRouting()).isFalse();

        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(current));
        IntegrationConflictException exception = assertThrows(IntegrationConflictException.class,
                () -> service.retryMessage(MESSAGE_ID, "still INVALID", "integrator-1"));
        assertThat(exception.code()).isEqualTo(IntegrationErrorCodes.INTEGRATION_MESSAGE_DEAD_LETTERED);
    }

    @Test
    void successfulRetryClearsRetrySchedulingStateAndMarksTheMessageRoutable() throws Exception {
        IntegrationMessageRecord failing = failedRecord(0, clock.instant());
        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(failing));
        when(adapterPort.normalizeMessage(any(), anyString())).thenReturn(
                new com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain
                        .NormalizedClinicalMessage("hl7v2.message", java.util.Map.of("patientId", "P1"), "unrouted"));
        when(adapterPort.acknowledgeMessage(anyString(), anyString(), anyString())).thenAnswer(invocation ->
                new IntegrationAcknowledgement(
                        invocation.getArgument(0), invocation.getArgument(1),
                        invocation.getArgument(2), null));

        clock.advanceSeconds(31);
        IntegrationMessageRecord retried = service.retryMessage(MESSAGE_ID, "patientId=P1", "integrator-1");

        assertThat(retried.normalizationStatus()).isEqualTo(IntegrationMessageRecord.STATUS_ACKNOWLEDGED);
        assertThat(retried.nextRetryAt()).isNull();
        assertThat(retried.deadLetterReason()).isNull();
        assertThat(retried.isReadyForDownstreamRouting()).isTrue();
    }

    private IntegrationMessageRecord failedRecord(int retryCount, Instant nextRetryAt) {
        ExternalMessageEnvelope envelope = new ExternalMessageEnvelope("hl7v2", "ref-1", clock.instant());
        String correlationId = IntegrationManagementService.deriveCorrelationId(ENDPOINT_ID, "EXT-1");
        AuditMetadata audit = new AuditMetadata("integrator-1", LocalDateTime.now(clock), "integrator-1",
                LocalDateTime.now(clock));
        return new IntegrationMessageRecord(
                MESSAGE_ID, ENDPOINT_ID, "EXT-1", envelope, correlationId, null, null, null,
                IntegrationMessageRecord.STATUS_NORMALIZATION_FAILED, "INTEGRATION_NORMALIZATION_FAILED", retryCount,
                nextRetryAt.atZone(ZoneOffset.UTC).toLocalDateTime(), null, audit);
    }

    /** Minimal mutable {@link Clock} letting tests deterministically fast-forward time. */
    private static final class AdjustableClock extends Clock {
        private Instant instant;

        AdjustableClock(Instant instant) {
            this.instant = instant;
        }

        void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
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
