package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.time.Instant;

/**
 * Append-only chain-of-custody trace entry (BCM-LAB-002, VO-COL-007).
 * Every Sample state transition appends one event; entries are immutable once recorded.
 */
public record ChainOfCustodyEvent(
        CustodyEventType eventType,
        String actorId,
        Instant occurredAt,
        String locationBranchId) {
}
