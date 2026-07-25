package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterAcknowledgement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterPort;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecord;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecordRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

class BillingEventServiceTest {

    private BillingEventRecordRepository recordRepository;
    private BillingAdapterPort adapterPort;
    private BillingEventService service;

    @BeforeEach
    void setUp() {
        recordRepository = mock(BillingEventRecordRepository.class);
        adapterPort = mock(BillingAdapterPort.class);
        when(recordRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        service = new BillingEventService(recordRepository, adapterPort, mock(AuditRecorder.class), clock);
    }

    @Test
    void publishBillingEventPersistsAnAcceptedRecord() {
        when(adapterPort.submitBillingEvent("tenant-1", "ent-1", "charge", 100, "USD", "ref-1"))
                .thenReturn(new BillingAdapterAcknowledgement("ref-1", BillingAdapterAcknowledgement.STATUS_ACCEPTED, null));

        BillingEventRecord record = service.publishBillingEvent(
                "tenant-1", "ent-1", "charge", 100, "USD", "ref-1", "operator-1");
        assertThat(record.adapterStatus()).isEqualTo(BillingAdapterAcknowledgement.STATUS_ACCEPTED);
        assertThat(record.providerReference()).isEqualTo("ref-1");
    }

    @Test
    void publishBillingEventPersistsARejectedRecordAndRethrowsWhenAdapterFails() {
        when(adapterPort.submitBillingEvent("tenant-1", "ent-1", "charge", 100, "EUR", null))
                .thenThrow(new BillingAdapterException("bad currency", "MARKETPLACE_COMMAND_INVALID"));

        BillingAdapterException exception = assertThrows(BillingAdapterException.class,
                () -> service.publishBillingEvent("tenant-1", "ent-1", "charge", 100, "EUR", null, "operator-1"));
        assertThat(exception.canonicalErrorCode()).isEqualTo("MARKETPLACE_COMMAND_INVALID");
    }

    @Test
    void publishBillingEventIsIdempotentForAnAlreadyAcceptedProviderReference() {
        BillingEventRecord existing = fixtureRecord(BillingAdapterAcknowledgement.STATUS_ACCEPTED, 0);
        when(recordRepository.findByTenantIdAndProviderReference("tenant-1", "ref-1")).thenReturn(Optional.of(existing));

        BillingEventRecord result = service.publishBillingEvent(
                "tenant-1", "ent-1", "charge", 100, "USD", "ref-1", "operator-1");

        assertThat(result).isEqualTo(existing);
        verify(adapterPort, never()).submitBillingEvent(any(), any(), any(), anyLong(), any(), any());
    }

    @Test
    void publishBillingEventRetriesAPreviouslyRejectedProviderReference() {
        BillingEventRecord existing = fixtureRecord(BillingAdapterAcknowledgement.STATUS_REJECTED, 0);
        when(recordRepository.findByTenantIdAndProviderReference("tenant-1", "ref-1")).thenReturn(Optional.of(existing));
        when(adapterPort.retrySubmission("tenant-1", "ref-1", "ent-1", "charge", 100, "USD"))
                .thenReturn(new BillingAdapterAcknowledgement("ref-1", BillingAdapterAcknowledgement.STATUS_ACCEPTED, null));

        BillingEventRecord retried = service.publishBillingEvent(
                "tenant-1", "ent-1", "charge", 100, "USD", "ref-1", "operator-1");

        assertThat(retried.adapterStatus()).isEqualTo(BillingAdapterAcknowledgement.STATUS_ACCEPTED);
        assertThat(retried.retryCount()).isEqualTo(1);
        assertThat(retried.billingEventId()).isEqualTo(existing.billingEventId());
    }

    @Test
    void publishBillingEventRethrowsAndIncrementsRetryCountWhenRetryIsRejectedAgain() {
        BillingEventRecord existing = fixtureRecord(BillingAdapterAcknowledgement.STATUS_REJECTED, 1);
        when(recordRepository.findByTenantIdAndProviderReference("tenant-1", "ref-1")).thenReturn(Optional.of(existing));
        when(adapterPort.retrySubmission("tenant-1", "ref-1", "ent-1", "charge", 100, "USD"))
                .thenThrow(new BillingAdapterException("still down", "PROVIDER_ADAPTER_UNAVAILABLE"));

        BillingAdapterException exception = assertThrows(BillingAdapterException.class, () -> service.publishBillingEvent(
                "tenant-1", "ent-1", "charge", 100, "USD", "ref-1", "operator-1"));
        assertThat(exception.canonicalErrorCode()).isEqualTo("PROVIDER_ADAPTER_UNAVAILABLE");
    }

    private BillingEventRecord fixtureRecord(String adapterStatus, int retryCount) {
        LocalDateTime now = LocalDateTime.now();
        return new BillingEventRecord("event-1", "tenant-1", "ent-1", "charge", 100, "USD", "ref-1", adapterStatus,
                retryCount, new AuditMetadata("operator-1", now, "operator-1", now));
    }
}
