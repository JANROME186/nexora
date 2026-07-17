package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application;

/**
 * Command to receive a labeled sample at the laboratory (BCM-LAB-005).
 *
 * <p>Custom implementation points (deferred to MVP-MOD-006-BE-002):
 * <ul>
 *   <li>CUS-LAB-005-01: Multi-criterion condition check (RN-002).</li>
 *   <li>CUS-LAB-005-02: Labeled-status precondition guard (RN-001).</li>
 * </ul>
 */
public record ReceiveSampleCommand(
        String sampleId,
        String tenantId,
        String receivedBy,
        String conditionAtReception) {
}
