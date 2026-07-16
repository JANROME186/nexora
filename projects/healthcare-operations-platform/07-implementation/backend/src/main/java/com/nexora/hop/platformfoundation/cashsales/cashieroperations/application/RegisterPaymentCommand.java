package com.nexora.hop.platformfoundation.cashsales.cashieroperations.application;

import java.math.BigDecimal;

public record RegisterPaymentCommand(
        BigDecimal amount,
        String currency,
        String method,
        String sessionId,
        String reference,
        String registeredBy) {
}
