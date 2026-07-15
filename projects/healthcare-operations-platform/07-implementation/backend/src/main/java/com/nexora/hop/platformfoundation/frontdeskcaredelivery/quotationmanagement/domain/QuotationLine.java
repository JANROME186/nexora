package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

/** A published test or panel included in the quotation, captured as a catalog snapshot (VO-QUO-001). */
public record QuotationLine(
        String lineId,
        String quotationId,
        String testDefinitionId,
        String catalogItemKind,
        int publishedVersion,
        int quantity,
        Money unitAmount) {

    public static final String KIND_TEST = "test";
    public static final String KIND_PANEL = "panel";
}
