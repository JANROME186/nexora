package com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

public record CashSession(
        String sessionId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String openedBy,
        Money openingAmount,
        Money expectedAmount,
        Money countedAmount,
        Money varianceAmount,
        String varianceReason,
        String status,
        Instant openedAt,
        Instant closedAt) {

    public static final String STATUS_OPEN = "open";
    public static final String STATUS_CLOSED = "closed";
}
