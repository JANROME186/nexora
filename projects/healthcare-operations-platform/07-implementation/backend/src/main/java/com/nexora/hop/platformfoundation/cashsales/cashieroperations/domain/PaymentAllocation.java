package com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

public record PaymentAllocation(
        String paymentId,
        String saleId,
        String sessionId,
        Money amount,
        String method,
        String reference,
        String registeredBy,
        Instant registeredAt) {

    public static final String METHOD_CASH = "cash";
    public static final String METHOD_CARD = "card";
    public static final String METHOD_TRANSFER = "transfer";
    public static final String METHOD_WALLET = "wallet";
    public static final String METHOD_MIXED = "mixed";
}
