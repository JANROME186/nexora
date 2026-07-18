package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

import java.util.Map;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Child entity of {@link IntegrationEndpoint} (ENT-INT-002). One record per external message
 * received on an endpoint, keyed for idempotent reprocessing by {@code (endpointId,
 * externalMessageId)} (INV-INT-002, RN-003). Stores the normalized snapshot inline (messageType /
 * canonicalFields / targetBoundedContext) alongside the opaque {@link ExternalMessageEnvelope}
 * for read-model and audit purposes.
 */
public record IntegrationMessageRecord(
        String messageId,
        String endpointId,
        String externalMessageId,
        ExternalMessageEnvelope envelope,
        String normalizedMessageType,
        Map<String, String> canonicalFields,
        String targetBoundedContext,
        String normalizationStatus,
        String canonicalErrorCode,
        int retryCount,
        AuditMetadata audit) {

    public static final String STATUS_RECEIVED = "received";
    public static final String STATUS_NORMALIZED = "normalized";
    public static final String STATUS_NORMALIZATION_FAILED = "normalization_failed";
    public static final String STATUS_ACKNOWLEDGED = "acknowledged";
    public static final String STATUS_RETRYING = "retrying";
    public static final String STATUS_DEAD_LETTERED = "dead_lettered";
}
