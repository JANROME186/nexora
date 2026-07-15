package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain;

import java.time.Instant;

/**
 * Immutable copy of referring-doctor identity fields captured on a {@link DiagnosticOrder} at
 * order time from BCM-PER-003 (VO-ORD-002). {@code licenseNumber} is sourced from the doctor's
 * masked primary document number, since BCM-PER-003's cross-module {@code DoctorSnapshot} does not
 * expose a separate unmasked professional-license field to downstream contexts.
 */
public record DoctorSnapshot(
        String doctorId,
        int sourceVersion,
        String fullName,
        String licenseNumber,
        Instant capturedAt) {
}
