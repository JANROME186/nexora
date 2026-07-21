package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Child entity of BCM-PLT-005 (ENT-APIM-003). One requests-per-minute policy per classification.
 * <p>
 * {@code consumerIdentificationMethod} (added by COM-MOD-011-BE-001, materially reducing
 * TD-BE-015) declares how the enforcement middleware attributes an incoming request to a consumer
 * bucket for the fixed-window counter. {@link #METHOD_PARTNER_API_KEY} is the only method valid
 * for the {@code partner} classification tier; {@link #METHOD_IP_ADDRESS} and
 * {@link #METHOD_SESSION_TOKEN} are the two methods available for the anonymous {@code public}
 * classification tier used by COM-MOD-011's public website surfaces (BCM-SVC-001/002/003/005 and
 * BCM-ATT-001/006).
 */
public record RateLimitPolicy(
        String policyId,
        String classification,
        int requestsPerMinute,
        String consumerIdentificationMethod,
        AuditMetadata audit) {

    public static final String METHOD_PARTNER_API_KEY = "partner_api_key";
    public static final String METHOD_IP_ADDRESS = "ip_address";
    public static final String METHOD_SESSION_TOKEN = "session_token";
}
