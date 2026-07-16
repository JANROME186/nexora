package com.nexora.hop.platformfoundation.cashsales.shared;

public final class CashSalesErrorCodes {

    public static final String SALE_SOURCE_NOT_ACCEPTED = "SALE_SOURCE_NOT_ACCEPTED";
    public static final String CASH_SESSION_REQUIRED = "CASH_SESSION_REQUIRED";
    public static final String PAYMENT_EXCEEDS_OUTSTANDING_BALANCE = "PAYMENT_EXCEEDS_OUTSTANDING_BALANCE";
    public static final String CASH_VARIANCE_REASON_REQUIRED = "CASH_VARIANCE_REASON_REQUIRED";
    public static final String BILLING_BOUNDARY_VIOLATION = "BILLING_BOUNDARY_VIOLATION";
    public static final String SALE_TERMINAL_STATE_IMMUTABLE = "SALE_TERMINAL_STATE_IMMUTABLE";
    public static final String BILLING_SALE_REQUIRED = "BILLING_SALE_REQUIRED";
    public static final String FISCAL_PROFILE_SNAPSHOT_REQUIRED = "FISCAL_PROFILE_SNAPSHOT_REQUIRED";
    public static final String BILLING_ADAPTER_NOT_CONFIGURED = "BILLING_ADAPTER_NOT_CONFIGURED";
    public static final String BILLING_REQUEST_INVALID_STATE_TRANSITION = "BILLING_REQUEST_INVALID_STATE_TRANSITION";
    public static final String BILLING_ADAPTER_TRANSIENT_ERROR = "BILLING_ADAPTER_TRANSIENT_ERROR";
    public static final String BILLING_ADAPTER_TERMINAL_ERROR = "BILLING_ADAPTER_TERMINAL_ERROR";

    private CashSalesErrorCodes() {
    }
}
