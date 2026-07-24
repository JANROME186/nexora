package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterAcknowledgement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterPort;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecord;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecordRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.InvalidMarketplaceCommandException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Compiles the generatable {@code publishBillingEvent} output by delegating to
 * {@link BillingAdapterPort} and persisting an observability-only {@link BillingEventRecord}
 * (INV-MKT-003, OFFER-004).
 */
@Service
public class BillingEventService {

    private final BillingEventRecordRepository recordRepository;
    private final BillingAdapterPort adapterPort;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public BillingEventService(
            BillingEventRecordRepository recordRepository, BillingAdapterPort adapterPort,
            AuditRecorder auditRecorder) {
        this(recordRepository, adapterPort, auditRecorder, Clock.systemUTC());
    }

    BillingEventService(
            BillingEventRecordRepository recordRepository, BillingAdapterPort adapterPort,
            AuditRecorder auditRecorder, Clock clock) {
        this.recordRepository = recordRepository;
        this.adapterPort = adapterPort;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public BillingEventRecord publishBillingEvent(
            String tenantId, String entitlementId, String eventType, long amountMinorUnits, String currency,
            String providerReference, String actorId) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        String type = requiredText(eventType, "Event type is required.");
        String actor = requiredText(actorId, "Actor id is required.");

        BillingAdapterAcknowledgement ack;
        String adapterStatus;
        try {
            ack = adapterPort.submitBillingEvent(tenant, entitlementId, type, amountMinorUnits, currency, providerReference);
            adapterStatus = ack.adapterStatus();
        } catch (BillingAdapterException exception) {
            adapterStatus = BillingAdapterAcknowledgement.STATUS_REJECTED;
            LocalDateTime now = LocalDateTime.now(clock);
            BillingEventRecord rejected = recordRepository.save(new BillingEventRecord(
                    newId(), tenant, entitlementId, type, amountMinorUnits, currency, providerReference,
                    adapterStatus, new AuditMetadata(actor, now, actor, now)));
            auditRecorder.recordSystemEvent(tenant, "BillingEventRejected", "BillingEventRecord",
                    rejected.billingEventId(), "{\"canonicalErrorCode\":\"%s\"}".formatted(exception.canonicalErrorCode()));
            throw exception;
        }

        LocalDateTime now = LocalDateTime.now(clock);
        BillingEventRecord saved = recordRepository.save(new BillingEventRecord(
                newId(), tenant, entitlementId, type, amountMinorUnits, currency, ack.providerReference(),
                adapterStatus, new AuditMetadata(actor, now, actor, now)));
        auditRecorder.recordSystemEvent(tenant, "BillingEventPublished", "BillingEventRecord",
                saved.billingEventId(), "{\"eventType\":\"%s\"}".formatted(type));
        return saved;
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidMarketplaceCommandException(message, MarketplaceErrorCodes.MARKETPLACE_COMMAND_INVALID);
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
