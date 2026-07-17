package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;

/**
 * Immutable copy of the applicable reference range (BCM-LAB-006, VO-LPR-002).
 * Captured at result-capture time from BCM-SVC-006; never re-read live.
 */
public record ReferenceRangeSnapshot(
        String referenceRangeId,
        int publishedVersion,
        String lowValue,
        String highValue,
        String criticalLowValue,
        String criticalHighValue,
        Instant capturedAt) {
}
