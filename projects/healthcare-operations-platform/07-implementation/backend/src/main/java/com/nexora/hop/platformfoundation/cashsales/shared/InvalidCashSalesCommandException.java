package com.nexora.hop.platformfoundation.cashsales.shared;

public class InvalidCashSalesCommandException extends RuntimeException {

    public InvalidCashSalesCommandException(String message) {
        super(message);
    }
}
