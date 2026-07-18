package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Root aggregate of BCM-PLT-004 Integration Management (ENT-INT-001). Registers an inbound,
 * outbound or bidirectional system-to-system integration surface (HL7v2, ASTM, FHIR, DICOM or a
 * generic REST webhook). Never parses raw payloads itself (INV-INT-001) and never issues commands
 * against business aggregates directly (INV-INT-003).
 */
public record IntegrationEndpoint(
        String endpointId,
        String tenantId,
        String laboratoryId,
        String endpointName,
        String protocol,
        String direction,
        String status,
        AuditMetadata audit) {

    public static final String PROTOCOL_HL7V2 = "hl7v2";
    public static final String PROTOCOL_ASTM = "astm";
    public static final String PROTOCOL_FHIR = "fhir";
    public static final String PROTOCOL_DICOM = "dicom";
    public static final String PROTOCOL_GENERIC_REST_WEBHOOK = "generic_rest_webhook";

    public static final String DIRECTION_INBOUND = "inbound";
    public static final String DIRECTION_OUTBOUND = "outbound";
    public static final String DIRECTION_BIDIRECTIONAL = "bidirectional";

    public static final String STATUS_REGISTERED = "registered";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_SUSPENDED = "suspended";
    public static final String STATUS_RETIRED = "retired";

    /** INV-INT-004: a retired endpoint can never send or receive messages again. */
    public boolean canExchangeMessages() {
        return !STATUS_RETIRED.equals(status) && !STATUS_SUSPENDED.equals(status);
    }
}
