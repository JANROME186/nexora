package com.nexora.hop.platformfoundation.cashsales.cashieroperations.application;

import java.math.BigDecimal;

public record OpenCashSessionCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String openedBy,
        BigDecimal openingAmount,
        String currency) {
}
