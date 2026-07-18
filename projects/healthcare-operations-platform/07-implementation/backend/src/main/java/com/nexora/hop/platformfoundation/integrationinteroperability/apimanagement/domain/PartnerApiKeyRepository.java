package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain;

import java.util.List;
import java.util.Optional;

public interface PartnerApiKeyRepository {

    PartnerApiKey save(PartnerApiKey key);

    Optional<PartnerApiKey> findById(String keyId);

    List<PartnerApiKey> findByTenantId(String tenantId);
}
