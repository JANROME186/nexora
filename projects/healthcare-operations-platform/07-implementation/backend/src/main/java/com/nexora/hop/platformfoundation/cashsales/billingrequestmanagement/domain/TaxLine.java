package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain;

import java.math.BigDecimal;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

public record TaxLine(
        String taxLineId,
        String invoiceRequestId,
        Money baseAmount,
        String taxCode,
        BigDecimal taxRate,
        Money taxAmount) {
}
