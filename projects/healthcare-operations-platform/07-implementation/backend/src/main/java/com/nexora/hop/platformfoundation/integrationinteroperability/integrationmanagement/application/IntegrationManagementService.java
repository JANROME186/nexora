package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.ExternalMessageEnvelope;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAcknowledgement;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAdapterException;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAdapterPort;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpoint;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpointRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecord;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecordRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.NormalizedClinicalMessage;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationConflictException;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationEntityNotFoundException;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationErrorCodes;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.InvalidIntegrationCommandException;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles the generatable outputs of bcm-plt-004-integration-management/generation-plan.yaml
 * (endpoint registration/listing/retirement, PRC-INT-004-01) and implements the CUS-INT-004-01/02
 * /03 custom rules delivered by MVP-MOD-008-BE-001: delegated raw-payload handling via
 * {@link IntegrationAdapterPort} (RN-001), canonical-error-only normalization failures (RN-002)
 * and idempotent message reprocessing keyed by {@code (endpointId, externalMessageId)}
 * (RN-003, INV-INT-002). Bounded retry/dead-letter policy (CUS-INT-004-04) and correlation-id
 * propagation (CUS-INT-004-05) remain explicit MVP-MOD-008-BE-002 scope; {@link #retryMessage}
 * only enforces a basic retry-count ceiling here.
 */
@Service
public class IntegrationManagementService {

    /** Basic retry ceiling; bounded backoff/dead-letter policy is MVP-MOD-008-BE-002 scope. */
    static final int BASIC_MAX_RETRY_COUNT = 5;

    private static final Set<String> VALID_PROTOCOLS = Set.of(
            IntegrationEndpoint.PROTOCOL_HL7V2, IntegrationEndpoint.PROTOCOL_ASTM,
            IntegrationEndpoint.PROTOCOL_FHIR, IntegrationEndpoint.PROTOCOL_DICOM,
            IntegrationEndpoint.PROTOCOL_GENERIC_REST_WEBHOOK);
    private static final Set<String> VALID_DIRECTIONS = Set.of(
            IntegrationEndpoint.DIRECTION_INBOUND, IntegrationEndpoint.DIRECTION_OUTBOUND,
            IntegrationEndpoint.DIRECTION_BIDIRECTIONAL);

    private final IntegrationEndpointRepository endpointRepository;
    private final IntegrationMessageRecordRepository messageRepository;
    private final IntegrationAdapterPort adapterPort;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public IntegrationManagementService(
            IntegrationEndpointRepository endpointRepository,
            IntegrationMessageRecordRepository messageRepository,
            IntegrationAdapterPort adapterPort,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(endpointRepository, messageRepository, adapterPort, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    IntegrationManagementService(
            IntegrationEndpointRepository endpointRepository,
            IntegrationMessageRecordRepository messageRepository,
            IntegrationAdapterPort adapterPort,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.endpointRepository = endpointRepository;
        this.messageRepository = messageRepository;
        this.adapterPort = adapterPort;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public IntegrationEndpoint registerEndpoint(
            String tenantId, String laboratoryId, String endpointName, String protocol, String direction,
            String actorId) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        if (!tenantDirectory.tenantExists(tenant)) {
            throw new IntegrationEntityNotFoundException("Tenant was not found.", "TENANT_NOT_FOUND");
        }
        String laboratory = requiredText(laboratoryId, "Laboratory id is required.");
        String name = requiredText(endpointName, "Endpoint name is required.");
        if (protocol == null || !VALID_PROTOCOLS.contains(protocol)) {
            throw new InvalidIntegrationCommandException(
                    "Protocol must be one of " + VALID_PROTOCOLS + ".", "INTEGRATION_PROTOCOL_INVALID");
        }
        if (direction == null || !VALID_DIRECTIONS.contains(direction)) {
            throw new InvalidIntegrationCommandException(
                    "Direction must be one of " + VALID_DIRECTIONS + ".", "INTEGRATION_DIRECTION_INVALID");
        }
        String actor = requiredText(actorId, "Actor id is required.");

        LocalDateTime now = LocalDateTime.now(clock);
        IntegrationEndpoint endpoint = new IntegrationEndpoint(
                newId(), tenant, laboratory, name, protocol, direction, IntegrationEndpoint.STATUS_REGISTERED,
                new com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata(actor, now, actor, now));
        IntegrationEndpoint saved = endpointRepository.save(endpoint);
        auditRecorder.recordSystemEvent(tenant, "IntegrationEndpointRegistered", "IntegrationEndpoint",
                saved.endpointId(), "{\"protocol\":\"%s\",\"direction\":\"%s\"}".formatted(protocol, direction));
        return saved;
    }

    public List<IntegrationEndpoint> listEndpoints(String tenantId) {
        return endpointRepository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    public IntegrationEndpoint getEndpoint(String endpointId) {
        return requireEndpoint(endpointId);
    }

    public IntegrationEndpoint retireEndpoint(String endpointId, String actorId) {
        IntegrationEndpoint current = requireEndpoint(endpointId);
        IntegrationEndpoint retired = new IntegrationEndpoint(
                current.endpointId(), current.tenantId(), current.laboratoryId(), current.endpointName(),
                current.protocol(), current.direction(), IntegrationEndpoint.STATUS_RETIRED,
                touched(current.audit(), actorId));
        IntegrationEndpoint saved = endpointRepository.save(retired);
        auditRecorder.recordSystemEvent(saved.tenantId(), "IntegrationEndpointRetired", "IntegrationEndpoint",
                saved.endpointId(), "{}");
        return saved;
    }

    /**
     * RN-003/INV-INT-002 idempotent receipt: a second delivery of the same
     * {@code (endpointId, externalMessageId)} returns the already-processed record rather than
     * reprocessing it. RN-001/RN-002: the raw payload is only ever handed to
     * {@link IntegrationAdapterPort}; a normalization failure is captured as a canonical error
     * code on the record, never surfaced as raw provider text.
     */
    public IntegrationMessageRecord receiveMessage(
            String endpointId, String externalMessageId, String rawPayload, String actorId) {
        IntegrationEndpoint endpoint = requireEndpoint(endpointId);
        if (!endpoint.canExchangeMessages()) {
            throw new IntegrationConflictException(
                    "Endpoint " + endpointId + " is " + endpoint.status() + " and cannot exchange messages.",
                    "INTEGRATION_ENDPOINT_NOT_ACTIVE");
        }
        String extMessageId = requiredText(externalMessageId, "External message id is required.");

        return messageRepository.findByEndpointIdAndExternalMessageId(endpointId, extMessageId)
                .orElseGet(() -> processNewMessage(endpoint, extMessageId, rawPayload, actorId));
    }

    private IntegrationMessageRecord processNewMessage(
            IntegrationEndpoint endpoint, String externalMessageId, String rawPayload, String actorId) {
        ExternalMessageEnvelope envelope = adapterPort.receiveMessage(rawPayload, endpoint.protocol());
        LocalDateTime now = LocalDateTime.now(clock);
        var audit = new com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata(actorId, now, actorId, now);

        IntegrationMessageRecord record;
        try {
            NormalizedClinicalMessage normalized = adapterPort.normalizeMessage(envelope, rawPayload);
            IntegrationAcknowledgement ack = adapterPort.acknowledgeMessage(
                    externalMessageId, IntegrationAcknowledgement.STATUS_ACCEPTED);
            record = new IntegrationMessageRecord(
                    newId(), endpoint.endpointId(), externalMessageId, envelope, normalized.messageType(),
                    normalized.canonicalFields(), normalized.targetBoundedContext(),
                    IntegrationMessageRecord.STATUS_ACKNOWLEDGED, ack.canonicalErrorCode(), 0, audit);
            messageRepository.save(record);
            auditRecorder.recordSystemEvent(endpoint.tenantId(), "MessageNormalized", "IntegrationMessageRecord",
                    record.messageId(), "{}");
        } catch (IntegrationAdapterException exception) {
            record = new IntegrationMessageRecord(
                    newId(), endpoint.endpointId(), externalMessageId, envelope, null, null, null,
                    IntegrationMessageRecord.STATUS_NORMALIZATION_FAILED, exception.canonicalErrorCode(), 0, audit);
            messageRepository.save(record);
            auditRecorder.recordSystemEvent(endpoint.tenantId(), "MessageNormalizationFailed",
                    "IntegrationMessageRecord", record.messageId(),
                    "{\"canonicalErrorCode\":\"%s\"}".formatted(exception.canonicalErrorCode()));
        }
        return record;
    }

    public IntegrationMessageRecord getMessage(String messageId) {
        return requireMessage(messageId);
    }

    /**
     * Basic retry: only allowed while normalization has not yet succeeded, bounded by
     * {@link #BASIC_MAX_RETRY_COUNT}. Bounded backoff timing and dead-letter transition
     * (CUS-INT-004-04) remain MVP-MOD-008-BE-002 scope.
     */
    public IntegrationMessageRecord retryMessage(String messageId, String rawPayload, String actorId) {
        IntegrationMessageRecord current = requireMessage(messageId);
        if (IntegrationMessageRecord.STATUS_ACKNOWLEDGED.equals(current.normalizationStatus())) {
            throw new IntegrationConflictException(
                    "Message " + messageId + " was already acknowledged; retry is not applicable.",
                    "INTEGRATION_RETRY_NOT_APPLICABLE");
        }
        if (current.retryCount() >= BASIC_MAX_RETRY_COUNT) {
            throw new IntegrationConflictException(
                    "Message " + messageId + " exceeded the retry limit of " + BASIC_MAX_RETRY_COUNT + ".",
                    IntegrationErrorCodes.INTEGRATION_RETRY_LIMIT_EXCEEDED);
        }
        IntegrationEndpoint endpoint = requireEndpoint(current.endpointId());

        IntegrationMessageRecord updated;
        try {
            NormalizedClinicalMessage normalized = adapterPort.normalizeMessage(current.envelope(), rawPayload);
            IntegrationAcknowledgement ack = adapterPort.acknowledgeMessage(
                    current.externalMessageId(), IntegrationAcknowledgement.STATUS_ACCEPTED);
            updated = new IntegrationMessageRecord(
                    current.messageId(), current.endpointId(), current.externalMessageId(), current.envelope(),
                    normalized.messageType(), normalized.canonicalFields(), normalized.targetBoundedContext(),
                    IntegrationMessageRecord.STATUS_ACKNOWLEDGED, ack.canonicalErrorCode(),
                    current.retryCount() + 1, touched(current.audit(), actorId));
        } catch (IntegrationAdapterException exception) {
            updated = new IntegrationMessageRecord(
                    current.messageId(), current.endpointId(), current.externalMessageId(), current.envelope(),
                    null, null, null, IntegrationMessageRecord.STATUS_NORMALIZATION_FAILED,
                    exception.canonicalErrorCode(), current.retryCount() + 1, touched(current.audit(), actorId));
        }
        IntegrationMessageRecord saved = messageRepository.save(updated);
        auditRecorder.recordSystemEvent(endpoint.tenantId(), "MessageRetryScheduled", "IntegrationMessageRecord",
                saved.messageId(), "{\"retryCount\":%d}".formatted(saved.retryCount()));
        return saved;
    }

    private IntegrationEndpoint requireEndpoint(String endpointId) {
        return endpointRepository.findById(requiredText(endpointId, "Endpoint id is required."))
                .orElseThrow(() -> new IntegrationEntityNotFoundException(
                        "Integration endpoint was not found.", "INTEGRATION_ENDPOINT_NOT_FOUND"));
    }

    private IntegrationMessageRecord requireMessage(String messageId) {
        return messageRepository.findById(requiredText(messageId, "Message id is required."))
                .orElseThrow(() -> new IntegrationEntityNotFoundException(
                        "Integration message record was not found.", "INTEGRATION_MESSAGE_NOT_FOUND"));
    }

    private static com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata touched(
            com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata audit, String actorId) {
        return new com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata(
                audit.createdBy(), audit.createdAt(), requiredText(actorId, "Actor id is required."),
                LocalDateTime.now());
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidIntegrationCommandException(message, "INTEGRATION_COMMAND_INVALID");
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
