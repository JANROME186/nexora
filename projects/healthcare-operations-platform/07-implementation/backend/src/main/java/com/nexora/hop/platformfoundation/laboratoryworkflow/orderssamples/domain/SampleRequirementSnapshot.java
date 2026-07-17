package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.time.Instant;

/**
 * Immutable copy of the published sample requirement captured at collection time (BCM-LAB-002, VO-COL-002).
 * Sourced from BCM-SVC-007 at collection time; never re-read live.
 */
public record SampleRequirementSnapshot(
        String sampleRequirementId,
        int publishedVersion,
        String containerType,
        String minimumVolume,
        String handlingInstructions,
        Instant capturedAt) {
}
