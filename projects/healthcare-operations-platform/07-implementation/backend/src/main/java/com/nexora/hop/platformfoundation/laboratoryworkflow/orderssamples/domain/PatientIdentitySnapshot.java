package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.time.Instant;

/**
 * Minimal immutable patient identity fields captured at collection time (BCM-LAB-002, VO-COL-001).
 * Never re-read live from BCM-PER-002; sourced from the DiagnosticOrder's own PatientSnapshot.
 */
public record PatientIdentitySnapshot(
        String patientId,
        String fullName,
        String birthDate,
        Instant capturedAt) {
}
