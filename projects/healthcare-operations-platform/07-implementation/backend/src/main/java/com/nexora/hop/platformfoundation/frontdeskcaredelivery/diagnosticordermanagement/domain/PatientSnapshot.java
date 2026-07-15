package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Immutable copy of patient identity fields captured on a {@link DiagnosticOrder} at order time
 * from BCM-PER-002 (modeled as VO-ORD-001 in bcm-lab-001-diagnostic-order-management/
 * business-model.yaml). Never re-read live to mutate the order; order pages resolve display data
 * from this snapshot (RN-001).
 */
public record PatientSnapshot(
        String patientId,
        int sourceVersion,
        String fullName,
        String documentType,
        String documentNumberMasked,
        LocalDate birthDate,
        Instant capturedAt) {
}
