package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

/**
 * @param correlationId echoes the caller-supplied correlation id back (RN-005, CUS-INT-004-05),
 *                       letting a replaceable adapter confirm it received and will propagate it.
 */
public record IntegrationAcknowledgement(
        String externalMessageId, String correlationId, String status, String canonicalErrorCode) {

    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_RETRYING = "retrying";
}
