package com.nexora.hop.platformfoundation.notificationmanagement.adapter.out.memory;

import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequestRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryNotificationRequestRepository implements NotificationRequestRepository {

    private final Map<UUID, NotificationRequest> store = new ConcurrentHashMap<>();

    @Override
    public NotificationRequest save(NotificationRequest request) {
        store.put(request.getNotificationId(), request);
        return request;
    }

    @Override
    public Optional<NotificationRequest> findById(UUID notificationId) {
        return Optional.ofNullable(store.get(notificationId));
    }

    @Override
    public List<NotificationRequest> findByRecipientId(String recipientId) {
        // Since original NotificationRequest does not store recipientId in the simplified baseline entity,
        // we can filter or adapt this as needed. Let's filter by checking if recipientAddress matches
        // or just return all for matching recipientId. If we align fields later, we can check getRecipientId.
        // For now, let's keep it safe.
        List<NotificationRequest> results = new ArrayList<>();
        for (NotificationRequest req : store.values()) {
            // Check if address matches or fallback
            if (req.getRecipientAddress() != null && req.getRecipientAddress().contains(recipientId)) {
                results.add(req);
            }
        }
        return results;
    }

    @Override
    public List<NotificationRequest> findAll() {
        return new ArrayList<>(store.values());
    }
}
