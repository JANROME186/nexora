package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application;

/**
 * Requested catalog item for a new order line, reused by callers across the module (order
 * creation, admission commit, quotation conversion) so line composition input has one shape.
 */
public record OrderLineInput(String testDefinitionId, String catalogItemKind, Integer quantity) {
}
