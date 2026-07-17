package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;

/**
 * Immutable copy of the analyte/test-definition (BCM-LAB-006, VO-LPR-001).
 * Captured at result-capture time from BCM-SVC-004; never re-read live.
 */
public record AnalyteSnapshot(
        String testDefinitionId,
        String analyteId,
        int publishedVersion,
        String name,
        String unit,
        String method,
        Instant capturedAt) {
}
