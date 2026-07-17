package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application;

/**
 * Command to reject a sample, either at collection or at reception.
 *
 * <p>Custom implementation points (deferred to MVP-MOD-006-BE-002):
 * <ul>
 *   <li>CUS-COL-002-04: Structured reason-code validation and terminal-state guard (RN-005).</li>
 * </ul>
 */
public record RejectSampleCommand(
        String sampleId,
        String tenantId,
        String rejectedBy,
        String rejectionStage,
        String reasonCode,
        String notes) {
}
