package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;

/**
 * Critical result flag (BCM-LAB-008, VO-LPR-006).
 * Owned and mutated exclusively by BCM-LAB-008; null unless FlagCriticalResult executes.
 */
public record CriticalResultFlag(
        String flaggedBy,
        Instant flaggedAt,
        String criticalReason) {
}
