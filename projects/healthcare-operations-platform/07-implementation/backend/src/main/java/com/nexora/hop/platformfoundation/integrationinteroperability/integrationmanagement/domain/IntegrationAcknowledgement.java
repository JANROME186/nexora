package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

public record IntegrationAcknowledgement(String externalMessageId, String status, String canonicalErrorCode) {

    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_RETRYING = "retrying";
}
