package com.nexora.hop.platformfoundation.frontdeskcaredelivery.application;

/**
 * Thrown by {@link FrontDeskSaleSourcePort} implementations when the requested diagnostic order
 * or quotation identifier does not exist. Keeps CashSales decoupled from FrontDeskCareDelivery
 * internal exception types.
 */
public class FrontDeskSourceNotFoundException extends RuntimeException {

    public FrontDeskSourceNotFoundException(String message) {
        super(message);
    }
}
