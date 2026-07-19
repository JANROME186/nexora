package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.out.memory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicy;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicyRepository;

@Repository
@Profile("!local")
class InMemoryRateLimitPolicyRepository implements RateLimitPolicyRepository {

    private final Map<String, RateLimitPolicy> policies = new ConcurrentHashMap<>();

    @Override
    public RateLimitPolicy save(RateLimitPolicy policy) {
        policies.put(policy.classification(), policy);
        return policy;
    }

    @Override
    public Optional<RateLimitPolicy> findByClassification(String classification) {
        return Optional.ofNullable(policies.get(classification));
    }

    @Override
    public Optional<RateLimitPolicy> findById(String policyId) {
        return policies.values().stream().filter(policy -> policy.policyId().equals(policyId)).findFirst();
    }
}
