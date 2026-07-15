package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application;

import java.math.BigDecimal;

public record IssueQuotationCommand(
        String currency,
        String discountKind,
        BigDecimal discountValue,
        Integer validityDays,
        boolean discountOverride) {
}
