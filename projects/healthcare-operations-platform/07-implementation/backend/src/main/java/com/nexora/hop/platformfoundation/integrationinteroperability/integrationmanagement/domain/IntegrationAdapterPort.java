package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

/**
 * Provider-agnostic outbound port (PORT-INT-001) mirroring
 * {@code cashsales.billingrequestmanagement.domain.FiscalAdapterPort},
 * {@code notificationmanagement.domain.NotificationProviderPort} and
 * {@code documentmanagement.domain.DocumentStoragePort}. Implementations must be replaceable with
 * a real protocol adapter (HL7v2, FHIR, ASTM, DICOM) without any application or domain code
 * change. No domain module may parse a raw payload directly (INV-INT-001) or read/mutate a
 * clinical, patient, order or catalog aggregate from this boundary.
 *
 * <p>Real open-source protocol parser adoption (e.g. HAPI FHIR, an open-source HL7v2 library) is
 * evaluated but not yet integrated (CUS-INT-004-06); see technical debt TD-BE-013.</p>
 */
public interface IntegrationAdapterPort {

    /**
     * Wraps a raw inbound payload as an opaque, referenceable envelope. Implementations must
     * never inline the raw payload bytes/text anywhere else in the domain model.
     *
     * @throws IntegrationAdapterException if the payload is missing or malformed
     */
    ExternalMessageEnvelope receiveMessage(String rawPayload, String protocolHint);

    /**
     * Normalizes a previously received envelope into a canonical, protocol-independent shape.
     *
     * @throws IntegrationAdapterException with a canonical error code (RN-002) if normalization
     *         fails; the raw provider error text must never be propagated
     */
    NormalizedClinicalMessage normalizeMessage(ExternalMessageEnvelope envelope, String rawPayload);

    /**
     * Builds the acknowledgement to report back for a processed external message.
     *
     * @param correlationId the caller-derived correlation id (RN-005, CUS-INT-004-05) to echo back
     *                      unchanged so it can be linked across every retry/outbound event
     */
    IntegrationAcknowledgement acknowledgeMessage(String externalMessageId, String correlationId, String status);
}
