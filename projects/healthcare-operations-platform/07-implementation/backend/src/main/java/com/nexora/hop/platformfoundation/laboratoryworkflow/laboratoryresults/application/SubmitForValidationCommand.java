package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application;

/**
 * Command to submit a result for technical validation (BCM-LAB-006).
 *
 * <p>Custom implementation points (deferred to MVP-MOD-006-BE-002):
 * <ul>
 *   <li>CUS-LPR-006-04: Unresolved-incident reliability judgment before allowing submission (RN-005).</li>
 * </ul>
 */
public record SubmitForValidationCommand(
        String resultId,
        String tenantId,
        String actorId) {
}
