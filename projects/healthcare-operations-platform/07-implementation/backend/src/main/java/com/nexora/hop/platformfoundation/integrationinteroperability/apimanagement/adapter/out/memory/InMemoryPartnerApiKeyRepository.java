package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKey;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKeyRepository;

@Repository
@Profile("!local")
class InMemoryPartnerApiKeyRepository implements PartnerApiKeyRepository {

    private final Map<String, PartnerApiKey> keys = new ConcurrentHashMap<>();

    @Override
    public PartnerApiKey save(PartnerApiKey key) {
        keys.put(key.keyId(), key);
        return key;
    }

    @Override
    public Optional<PartnerApiKey> findById(String keyId) {
        return Optional.ofNullable(keys.get(keyId));
    }

    @Override
    public List<PartnerApiKey> findByTenantId(String tenantId) {
        return keys.values().stream().filter(key -> key.tenantId().equals(tenantId)).toList();
    }
}
