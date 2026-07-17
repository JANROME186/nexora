package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application;

/**
 * Command to dispose a rejected or processed sample (BCM-LAB-005).
 *
 * <p>Custom implementation points (deferred to MVP-MOD-006-BE-002):
 * <ul>
 *   <li>CUS-LAB-005-03: Terminal-state precondition guard and evidence-preserving disposal record (RN-004).</li>
 * </ul>
 */
public record DisposeSampleCommand(
        String sampleId,
        String tenantId,
        String actorId,
        String disposalNotes) {
}
