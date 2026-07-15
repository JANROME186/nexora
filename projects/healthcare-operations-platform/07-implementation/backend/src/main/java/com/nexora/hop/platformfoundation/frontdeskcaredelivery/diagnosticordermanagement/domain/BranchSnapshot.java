package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain;

import java.time.Instant;

/**
 * Immutable copy of branch identity fields captured on a {@link DiagnosticOrder} at order time
 * from BCM-ORG-003 (VO-ORD-003).
 */
public record BranchSnapshot(
        String branchId,
        int sourceVersion,
        String name,
        Instant capturedAt) {
}
