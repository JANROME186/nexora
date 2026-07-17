package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ResultDeliveryTicketRepository {

    ResultDeliveryTicket save(ResultDeliveryTicket ticket);

    Optional<ResultDeliveryTicket> findById(UUID ticketId);

    List<ResultDeliveryTicket> findByResultId(ResultId resultId);

    List<ResultDeliveryTicket> findByRecipientId(String recipientId);

    List<ResultDeliveryTicket> findAll();
}
