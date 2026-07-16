package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain;

/**
 * Provider-agnostic fiscal adapter response. Captures the outcome of a submit, retry or cancel
 * call without embedding country-specific fiscal document types.
 *
 * @param correlationId      the opaque provider reference for this request; may be null if the
 *                           provider did not return one
 * @param adapterStatus      normalised status string matching one of the InvoiceRequest status
 *                           constants: {@code submitted}, {@code issued}, {@code failed},
 *                           {@code cancelled}
 * @param rawResponseSnapshot optional raw provider response captured as a plain string; must not
 *                            contain secrets, PAN or credential data
 */
public record FiscalAdapterResponse(
        String correlationId,
        String adapterStatus,
        String rawResponseSnapshot) {

    public static final String STATUS_SUBMITTED = "submitted";
    public static final String STATUS_ISSUED = "issued";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_CANCELLED = "cancelled";
}
