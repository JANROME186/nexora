package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

/**
 * One catalog item (test or panel) requested within a {@link DiagnosticOrder}, captured as an
 * immutable catalog snapshot (ENT-ORD-002, VO-ORD-004). {@code unitAmount} is populated once the
 * order is priced.
 */
public record OrderLine(
        String orderLineId,
        String orderId,
        String testDefinitionId,
        String catalogItemKind,
        String catalogItemName,
        int catalogPublishedVersion,
        int quantity,
        Money unitAmount,
        String lineStatus) {

    public static final String KIND_TEST = "test";
    public static final String KIND_PANEL = "panel";

    public static final String LINE_PENDING = "pending";
    public static final String LINE_ACCEPTED = "accepted";
    public static final String LINE_CANCELLED = "cancelled";
    public static final String LINE_COMPLETED = "completed";
}
