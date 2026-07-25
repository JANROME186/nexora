package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

import java.time.Instant;

/**
 * Published-language value object (context-map.md REL-CTX-011): wraps an inbound raw message
 * as an opaque reference. No domain module ever inlines or parses the raw payload directly
 * (INV-INT-001) — {@code rawPayloadReference} is the only handle to it.
 */
public record ExternalMessageEnvelope(String sourceProtocol, String rawPayloadReference, Instant receivedAt) {
}
