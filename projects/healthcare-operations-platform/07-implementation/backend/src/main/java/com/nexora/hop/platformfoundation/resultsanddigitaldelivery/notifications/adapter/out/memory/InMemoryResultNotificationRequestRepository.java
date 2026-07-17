package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.adapter.out.memory;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain.ResultNotificationRequest;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain.ResultNotificationRequestRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryResultNotificationRequestRepository implements ResultNotificationRequestRepository {

    private final Map<UUID, ResultNotificationRequest> store = new ConcurrentHashMap<>();

    @Override
    public ResultNotificationRequest save(ResultNotificationRequest request) {
        store.put(request.getResultNotificationId(), request);
        return request;
    }

    @Override
    public Optional<ResultNotificationRequest> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ResultNotificationRequest> findByRecipientId(String recipientId) {
        List<ResultNotificationRequest> results = new ArrayList<>();
        for (ResultNotificationRequest req : store.values()) {
            if (recipientId.equals(req.getRecipientId())) {
                results.add(req);
            }
        }
        return results;
    }

    @Override
    public List<ResultNotificationRequest> findAll() {
        return new ArrayList<>(store.values());
    }
}
