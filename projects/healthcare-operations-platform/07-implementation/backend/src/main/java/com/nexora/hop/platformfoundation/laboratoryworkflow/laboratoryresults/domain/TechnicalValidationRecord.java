package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;

/**
 * Technical validation record (BCM-LAB-008, VO-LPR-005).
 * Owned and mutated exclusively by BCM-LAB-008; modeled as a field placeholder on the
 * shared LaboratoryResult aggregate. Null until PerformTechnicalValidation executes.
 */
public record TechnicalValidationRecord(
        String validatedBy,
        Instant validatedAt) {
}
