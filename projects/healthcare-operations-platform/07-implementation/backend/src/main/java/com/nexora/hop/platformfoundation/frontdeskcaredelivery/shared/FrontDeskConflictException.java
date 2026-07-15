package com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared;

/**
 * Raised when a Front Desk and Care Delivery command cannot be applied because it conflicts with
 * the current durable state of an aggregate: for example pricing an order with no lines, adding
 * an unpublished catalog item, or accepting an expired quotation.
 */
public class FrontDeskConflictException extends RuntimeException {

    public FrontDeskConflictException(String message) {
        super(message);
    }
}
