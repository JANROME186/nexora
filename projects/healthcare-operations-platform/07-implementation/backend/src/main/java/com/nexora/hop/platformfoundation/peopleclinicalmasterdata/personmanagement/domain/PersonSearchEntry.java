package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain;

import java.time.LocalDate;

/**
 * Unified read entry projecting Patient (AGG-001) and Doctor (AGG-005) into the person search
 * index (RM-PER-001). This projection is built on demand from the two owning aggregates. The
 * event-driven projection ordering described in BCM-PER-001 RN-004 stays deferred to
 * MVP-MOD-003-BE-002.
 */
public record PersonSearchEntry(
        String tenantId,
        String laboratoryId,
        String personKind,
        String sourceAggregateId,
        String personCode,
        String fullName,
        String normalizedFamilyName,
        String normalizedGivenName,
        LocalDate birthDate,
        String primaryDocumentType,
        String primaryDocumentNumberMasked,
        String status) {
}
