package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain;

public class BillingAdapterException extends RuntimeException {

    private final String canonicalErrorCode;

    public BillingAdapterException(String message, String canonicalErrorCode) {
        super(message);
        this.canonicalErrorCode = canonicalErrorCode;
    }

    public String canonicalErrorCode() {
        return canonicalErrorCode;
    }
}
