package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain;

/**
 * Candidate match returned by the duplicate detection service. The MVP-MOD-003-BE-001 compilation
 * only fills the simple structural fields; tenant-configurable weighted confidence scoring
 * (BCM-PER-001 RN-003) is deferred to MVP-MOD-003-BE-002.
 */
public record PersonDuplicateCandidate(
        String personKind,
        String sourceAggregateId,
        String fullName,
        double confidence,
        String matchReason) {
}
