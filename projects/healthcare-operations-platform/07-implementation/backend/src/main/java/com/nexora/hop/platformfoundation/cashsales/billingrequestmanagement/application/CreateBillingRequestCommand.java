package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.application;

import java.math.BigDecimal;

public record CreateBillingRequestCommand(
        String saleId,
        String legalName,
        String taxIdentifier,
        String fiscalAddress,
        String fiscalRegime,
        String taxCode,
        BigDecimal taxRate,
        String actorId) {
}
