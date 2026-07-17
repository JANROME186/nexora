package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;

/**
 * Medical validation record (BCM-LAB-009, VO-LPR-007).
 * Owned and mutated exclusively by BCM-LAB-009; null until PerformMedicalValidation executes.
 */
public record MedicalValidationRecord(
        String validatedBy,
        Instant validatedAt) {
}
