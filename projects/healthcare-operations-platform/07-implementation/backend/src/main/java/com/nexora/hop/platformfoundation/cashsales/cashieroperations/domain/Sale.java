package com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain;

import java.time.Instant;

public record Sale(
        String saleId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String patientId,
        String sourceType,
        String sourceReferenceId,
        SaleTotals totals,
        String status,
        String cancellationReason,
        String actorId,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String SOURCE_DIAGNOSTIC_ORDER = "diagnostic_order";
    public static final String SOURCE_QUOTATION = "quotation";

    public static final String STATUS_PAYABLE = "payable";
    public static final String STATUS_PARTIALLY_PAID = "partially_paid";
    public static final String STATUS_PAID = "paid";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_REFUNDED = "refunded";
}
