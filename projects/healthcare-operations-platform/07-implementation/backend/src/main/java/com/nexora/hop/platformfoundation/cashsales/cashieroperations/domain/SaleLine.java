package com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

public record SaleLine(
        String saleLineId,
        String saleId,
        String catalogItemId,
        String catalogItemKind,
        String descriptionSnapshot,
        int quantity,
        Money unitAmount,
        Money lineTotal) {
}
