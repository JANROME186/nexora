package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.time.Instant;

/**
 * Reception record (BCM-LAB-005, VO-COL-005).
 * Owned and mutated exclusively by BCM-LAB-005; modeled as a field placeholder on the
 * shared Sample aggregate. Null until ReceiveSampleAtLaboratory or RejectSampleAtReception executes.
 */
public record SampleReceptionRecord(
        String receivedBy,
        Instant receivedAt,
        ReceptionCondition conditionAtReception) {
}
