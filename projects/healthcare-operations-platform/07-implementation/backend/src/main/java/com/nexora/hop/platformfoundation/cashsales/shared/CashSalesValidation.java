package com.nexora.hop.platformfoundation.cashsales.shared;

public final class CashSalesValidation {

    private CashSalesValidation() {
    }

    public static String requiredText(String value, String message) {
        String normalized = optionalText(value);
        if (normalized == null) {
            throw new InvalidCashSalesCommandException(message);
        }
        return normalized;
    }

    public static String optionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
