package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;

/**
 * Result amendment record (BCM-LAB-010, VO-LPR-009).
 * Released results are immutable except through an explicit amendment (INV-LPR-004).
 */
public record ResultAmendment(
        String amendedBy,
        Instant amendedAt,
        String amendmentReason,
        ResultValue previousValue) {
}
