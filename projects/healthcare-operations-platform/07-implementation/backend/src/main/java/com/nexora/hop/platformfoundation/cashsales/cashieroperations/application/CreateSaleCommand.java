package com.nexora.hop.platformfoundation.cashsales.cashieroperations.application;

public record CreateSaleCommand(
        String tenantId,
        String sourceType,
        String sourceReferenceId,
        String actorId) {
}
