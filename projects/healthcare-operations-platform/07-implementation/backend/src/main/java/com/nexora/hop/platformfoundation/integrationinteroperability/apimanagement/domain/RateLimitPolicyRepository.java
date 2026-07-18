package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain;

import java.util.Optional;

public interface RateLimitPolicyRepository {

    RateLimitPolicy save(RateLimitPolicy policy);

    Optional<RateLimitPolicy> findByClassification(String classification);
}
