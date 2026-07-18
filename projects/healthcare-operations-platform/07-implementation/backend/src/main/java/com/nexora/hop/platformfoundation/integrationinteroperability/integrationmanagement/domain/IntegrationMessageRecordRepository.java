package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface IntegrationMessageRecordRepository {

    IntegrationMessageRecord save(IntegrationMessageRecord record);

    Optional<IntegrationMessageRecord> findById(String messageId);

    Optional<IntegrationMessageRecord> findByEndpointIdAndExternalMessageId(
            String endpointId, String externalMessageId);

    List<IntegrationMessageRecord> findByEndpointId(String endpointId);
}
