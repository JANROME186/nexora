package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;

/**
 * Result release record (BCM-LAB-010, VO-LPR-008).
 * Owned and mutated exclusively by BCM-LAB-010; null until ReleaseResult executes.
 */
public record ResultReleaseRecord(
        String releasedBy,
        Instant releasedAt) {
}
