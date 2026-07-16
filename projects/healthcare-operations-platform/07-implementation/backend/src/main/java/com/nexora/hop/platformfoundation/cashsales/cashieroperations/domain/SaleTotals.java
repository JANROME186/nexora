package com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

public record SaleTotals(
        Money subtotalAmount,
        Money discountAmount,
        Money totalAmount,
        Money paidAmount,
        Money outstandingAmount) {
}
