package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application;

/**
 * Command to collect a sample against an accepted diagnostic order line.
 *
 * <p>Custom implementation points (deferred to MVP-MOD-006-BE-002):
 * <ul>
 *   <li>CUS-COL-002-01: Multi-source snapshot capture (patient identity, sample requirement).</li>
 *   <li>CUS-COL-002-02: Order-line acceptance validation (RN-001).</li>
 *   <li>CUS-COL-002-03: Chain-of-custody atomic append (RN-004).</li>
 * </ul>
 */
public record CollectSampleCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String orderId,
        String orderLineId,
        String collectorId,
        String collectionSite,
        String collectionMethod,
        String containerUsed,
        String collectedAt,
        String patientConditionAtCollection,
        String patientId,
        String patientFullName,
        String patientBirthDate,
        String sampleRequirementId,
        String containerType) {
}
