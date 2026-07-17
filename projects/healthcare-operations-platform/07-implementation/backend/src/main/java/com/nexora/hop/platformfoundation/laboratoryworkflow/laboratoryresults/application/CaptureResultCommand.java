package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application;

import java.math.BigDecimal;

/**
 * Command to capture a laboratory result for a received sample.
 *
 * <p>Custom implementation points (deferred to MVP-MOD-006-BE-002):
 * <ul>
 *   <li>CUS-LPR-006-01: Multi-source snapshot capture (analyte, reference range) at result-capture time.</li>
 *   <li>CUS-LPR-006-02: Received-sample precondition check (INV-LPR-001, RN-001).</li>
 *   <li>CUS-LPR-006-03: Device-message boundary enforcement (INV-LPR-005, RN-004).</li>
 * </ul>
 */
public record CaptureResultCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String orderId,
        String sampleId,
        String rawValue,
        BigDecimal numericValue,
        String unit,
        String captureSource,
        String capturedBy,
        String deviceReference,
        String testDefinitionId,
        String analyteId,
        String analyteName,
        String referenceRangeId) {
}
