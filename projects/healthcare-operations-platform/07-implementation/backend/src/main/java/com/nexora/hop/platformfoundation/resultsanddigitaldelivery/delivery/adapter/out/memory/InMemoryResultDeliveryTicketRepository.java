package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.adapter.out.memory;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicket;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicketRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@org.springframework.context.annotation.Profile({"local", "test"})
public class InMemoryResultDeliveryTicketRepository implements ResultDeliveryTicketRepository {

    private final Map<UUID, ResultDeliveryTicket> store = new ConcurrentHashMap<>();

    @Override
    public ResultDeliveryTicket save(ResultDeliveryTicket ticket) {
        store.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    @Override
    public Optional<ResultDeliveryTicket> findById(UUID ticketId) {
        return Optional.ofNullable(store.get(ticketId));
    }

    @Override
    public List<ResultDeliveryTicket> findByResultId(ResultId resultId) {
        List<ResultDeliveryTicket> results = new ArrayList<>();
        for (ResultDeliveryTicket ticket : store.values()) {
            if (ticket.getResultId().equals(resultId)) {
                results.add(ticket);
            }
        }
        return results;
    }

    @Override
    public List<ResultDeliveryTicket> findByRecipientId(String recipientId) {
        List<ResultDeliveryTicket> results = new ArrayList<>();
        for (ResultDeliveryTicket ticket : store.values()) {
            if (recipientId.equals(ticket.getRecipientId())) {
                results.add(ticket);
            }
        }
        return results;
    }

    @Override
    public List<ResultDeliveryTicket> findAll() {
        return new ArrayList<>(store.values());
    }
}
