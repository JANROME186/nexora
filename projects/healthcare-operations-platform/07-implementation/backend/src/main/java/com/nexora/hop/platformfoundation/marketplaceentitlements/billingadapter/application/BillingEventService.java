package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
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
 *
 * <p>COM-MOD-017-BE-002 adds idempotency/retry (TD-BE-018): when the caller supplies a
 * non-blank {@code providerReference}, it is treated as an idempotency key. A prior {@code
 * accepted} record for the same {@code (tenantId, providerReference)} is returned as-is (no
 * duplicate provider call); a prior {@code rejected} record is retried via {@link
 * BillingAdapterPort#retrySubmission} and the existing record is updated in place with an
 * incremented {@code retryCount}, never superseded by a new billing event id. This never makes the
 * billing event an entitlement/license source of truth (INV-MKT-003 preserved).
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

        if (providerReference != null && !providerReference.isBlank()) {
            Optional<BillingEventRecord> existing =
                    recordRepository.findByTenantIdAndProviderReference(tenant, providerReference);
            if (existing.isPresent()) {
                return retryOrReplay(existing.get(), entitlementId, type, amountMinorUnits, currency, actor);
            }
        }

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
                    adapterStatus, 0, new AuditMetadata(actor, now, actor, now)));
            auditRecorder.recordSystemEvent(tenant, "BillingEventRejected", "BillingEventRecord",
                    rejected.billingEventId(), "{\"canonicalErrorCode\":\"%s\"}".formatted(exception.canonicalErrorCode()));
            throw exception;
        }

        LocalDateTime now = LocalDateTime.now(clock);
        BillingEventRecord saved = recordRepository.save(new BillingEventRecord(
                newId(), tenant, entitlementId, type, amountMinorUnits, currency, ack.providerReference(),
                adapterStatus, 0, new AuditMetadata(actor, now, actor, now)));
        auditRecorder.recordSystemEvent(tenant, "BillingEventPublished", "BillingEventRecord",
                saved.billingEventId(), "{\"eventType\":\"%s\"}".formatted(type));
        return saved;
    }

    /** Idempotent replay for an already-accepted record, or a real provider retry for a rejected one. */
    private BillingEventRecord retryOrReplay(
            BillingEventRecord existing, String entitlementId, String type, long amountMinorUnits, String currency,
            String actor) {
        if (BillingAdapterAcknowledgement.STATUS_ACCEPTED.equals(existing.adapterStatus())) {
            auditRecorder.recordSystemEvent(existing.tenantId(), "BillingEventIdempotentReplay", "BillingEventRecord",
                    existing.billingEventId(), "{}");
            return existing;
        }
        BillingAdapterAcknowledgement ack;
        try {
            ack = adapterPort.retrySubmission(
                    existing.tenantId(), existing.providerReference(), entitlementId, type, amountMinorUnits, currency);
        } catch (BillingAdapterException exception) {
            BillingEventRecord retried = recordRepository.save(new BillingEventRecord(
                    existing.billingEventId(), existing.tenantId(), entitlementId, type, amountMinorUnits, currency,
                    existing.providerReference(), BillingAdapterAcknowledgement.STATUS_REJECTED,
                    existing.retryCount() + 1, touched(existing.audit(), actor)));
            auditRecorder.recordSystemEvent(existing.tenantId(), "BillingEventRetryRejected", "BillingEventRecord",
                    retried.billingEventId(), "{\"canonicalErrorCode\":\"%s\"}".formatted(exception.canonicalErrorCode()));
            throw exception;
        }
        BillingEventRecord retried = recordRepository.save(new BillingEventRecord(
                existing.billingEventId(), existing.tenantId(), entitlementId, type, amountMinorUnits, currency,
                ack.providerReference(), ack.adapterStatus(), existing.retryCount() + 1, touched(existing.audit(), actor)));
        auditRecorder.recordSystemEvent(existing.tenantId(), "BillingEventRetrySucceeded", "BillingEventRecord",
                retried.billingEventId(), "{\"retryCount\":%d}".formatted(retried.retryCount()));
        return retried;
    }

    private AuditMetadata touched(AuditMetadata audit, String actorId) {
        return new AuditMetadata(audit.createdBy(), audit.createdAt(), actorId, LocalDateTime.now(clock));
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
