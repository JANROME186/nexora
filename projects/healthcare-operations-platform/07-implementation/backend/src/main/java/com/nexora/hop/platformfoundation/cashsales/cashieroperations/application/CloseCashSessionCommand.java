package com.nexora.hop.platformfoundation.cashsales.cashieroperations.application;

import java.math.BigDecimal;

public record CloseCashSessionCommand(
        BigDecimal countedAmount,
        String currency,
        String varianceReason) {
}
