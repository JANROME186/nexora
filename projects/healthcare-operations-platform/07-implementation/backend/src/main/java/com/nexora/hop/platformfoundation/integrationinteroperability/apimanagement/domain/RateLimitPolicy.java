package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Child entity of BCM-PLT-005 (ENT-APIM-003). One requests-per-minute policy per classification.
 * Enforcement middleware (RN-004) remains MVP-MOD-008-BE-002 scope; this compiles policy
 * configuration only.
 */
public record RateLimitPolicy(String policyId, String classification, int requestsPerMinute, AuditMetadata audit) {
}
