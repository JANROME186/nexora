package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.time.Instant;

/**
 * Specimen label information (BCM-LAB-003, VO-COL-004).
 * Owned and mutated exclusively by BCM-LAB-003; modeled as a field placeholder on the
 * shared Sample aggregate. Null until AssignSpecimenLabel executes.
 */
public record SpecimenLabelInfo(
        String labelId,
        String barcodeValue,
        Instant printedAt) {
}
