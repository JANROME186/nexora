package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.time.Instant;

/**
 * Structured rejection reason for a sample (BCM-LAB-002, VO-COL-006).
 * Capturable either at collection (BCM-LAB-002) or at reception (BCM-LAB-005).
 */
public record SampleRejectionReason(
        String rejectedBy,
        Instant rejectedAt,
        RejectionStage rejectionStage,
        RejectionReasonCode reasonCode,
        String notes) {
}
