package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application;

/**
 * Command to assign a specimen label to a collected sample (BCM-LAB-003).
 *
 * <p>Custom implementation points (deferred to MVP-MOD-006-BE-002):
 * <ul>
 *   <li>CUS-LAB-003-01: Sample-status precondition check (collected or in_transit only).</li>
 *   <li>CUS-LAB-003-02: Barcode generation and uniqueness enforcement.</li>
 * </ul>
 */
public record LabelSampleCommand(
        String sampleId,
        String tenantId,
        String labelId,
        String barcodeValue,
        String actorId) {
}
