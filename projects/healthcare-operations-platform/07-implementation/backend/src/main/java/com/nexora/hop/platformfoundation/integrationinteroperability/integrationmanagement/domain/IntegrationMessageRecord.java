package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

import java.time.LocalDateTime;
import java.util.Map;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Child entity of {@link IntegrationEndpoint} (ENT-INT-002). One record per external message
 * received on an endpoint, keyed for idempotent reprocessing by {@code (endpointId,
 * externalMessageId)} (INV-INT-002, RN-003). Stores the normalized snapshot inline (messageType /
 * canonicalFields / targetBoundedContext) alongside the opaque {@link ExternalMessageEnvelope}
 * for read-model and audit purposes.
 *
 * <p>{@code correlationId} is a stable identifier derived once at first receipt and propagated
 * unchanged across every retry attempt and adapter acknowledgement, linking a message's full
 * inbound/outbound lifecycle for audit (RN-005, CUS-INT-004-05). {@code nextRetryAt} and
 * {@code deadLetterReason} implement the bounded, auditable retry/dead-letter policy (RN-004,
 * CUS-INT-004-04): {@code nextRetryAt} gates how soon a failed message may be retried again, and
 * {@code deadLetterReason} is set only when {@link #STATUS_DEAD_LETTERED} is reached, recording
 * why the message stopped being retryable.</p>
 */
public record IntegrationMessageRecord(
        String messageId,
        String endpointId,
        String externalMessageId,
        ExternalMessageEnvelope envelope,
        String correlationId,
        String normalizedMessageType,
        Map<String, String> canonicalFields,
        String targetBoundedContext,
        String normalizationStatus,
        String canonicalErrorCode,
        int retryCount,
        LocalDateTime nextRetryAt,
        String deadLetterReason,
        AuditMetadata audit) {

    public static final String STATUS_RECEIVED = "received";
    public static final String STATUS_NORMALIZED = "normalized";
    public static final String STATUS_NORMALIZATION_FAILED = "normalization_failed";
    public static final String STATUS_ACKNOWLEDGED = "acknowledged";
    public static final String STATUS_RETRYING = "retrying";
    public static final String STATUS_DEAD_LETTERED = "dead_lettered";

    /**
     * RN-001: a message may only be handed to a target bounded context once it has been
     * successfully normalized and acknowledged; a failed, retrying or dead-lettered message never
     * reaches a domain module.
     */
    public boolean isReadyForDownstreamRouting() {
        return STATUS_ACKNOWLEDGED.equals(normalizationStatus);
    }
}
