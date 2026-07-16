package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain;

import java.time.Instant;

public record InvoiceRequest(
        String invoiceRequestId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String saleId,
        String patientId,
        FiscalProfileSnapshot fiscalProfileSnapshot,
        String status,
        String adapterCorrelationId,
        String adapterResponseSnapshot,
        String actorId,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_REQUESTED = "requested";
    public static final String STATUS_SUBMITTED = "submitted";
    public static final String STATUS_ISSUED = "issued";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_CANCELLED = "cancelled";
}
