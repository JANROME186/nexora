package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

import java.util.Map;

/**
 * Published-language value object: the canonical, protocol-independent shape a raw message is
 * normalized into before any owning domain command is invoked. This capability only produces
 * this value; it never invokes the owning domain command itself (INV-INT-003).
 */
public record NormalizedClinicalMessage(String messageType, Map<String, String> canonicalFields, String targetBoundedContext) {
}
