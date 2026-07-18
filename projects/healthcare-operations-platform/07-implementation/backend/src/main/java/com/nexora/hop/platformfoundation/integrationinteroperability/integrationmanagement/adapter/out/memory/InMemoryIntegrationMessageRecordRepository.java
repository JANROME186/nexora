package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecord;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecordRepository;

@Repository
@Profile("!local")
class InMemoryIntegrationMessageRecordRepository implements IntegrationMessageRecordRepository {

    private final Map<String, IntegrationMessageRecord> records = new ConcurrentHashMap<>();

    @Override
    public IntegrationMessageRecord save(IntegrationMessageRecord record) {
        records.put(record.messageId(), record);
        return record;
    }

    @Override
    public Optional<IntegrationMessageRecord> findById(String messageId) {
        return Optional.ofNullable(records.get(messageId));
    }

    @Override
    public Optional<IntegrationMessageRecord> findByEndpointIdAndExternalMessageId(
            String endpointId, String externalMessageId) {
        return records.values().stream()
                .filter(record -> record.endpointId().equals(endpointId)
                        && record.externalMessageId().equals(externalMessageId))
                .findFirst();
    }

    @Override
    public List<IntegrationMessageRecord> findByEndpointId(String endpointId) {
        return records.values().stream().filter(record -> record.endpointId().equals(endpointId)).toList();
    }
}
