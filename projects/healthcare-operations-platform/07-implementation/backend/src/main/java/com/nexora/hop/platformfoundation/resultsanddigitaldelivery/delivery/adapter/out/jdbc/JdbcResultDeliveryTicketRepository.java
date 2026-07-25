package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.*;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("!local & !test")
public class JdbcResultDeliveryTicketRepository implements ResultDeliveryTicketRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcResultDeliveryTicketRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Stub implementation to satisfy technical debt TD-DB-001 for now.
    // Complete mapping will be implemented when non-local durability is required.

    public ResultDeliveryTicket save(ResultDeliveryTicket entity) {
        throw new UnsupportedOperationException("JDBC implementation pending full field mapping");
    }

    public Optional<ResultDeliveryTicket> findById(UUID id) {
        return Optional.empty();
    }

    public Optional<ResultDeliveryTicket> findByPatientId(PatientId id) {
        return Optional.empty();
    }

    public List<ResultDeliveryTicket> findByResultId(ResultId resultId) {
        return List.of();
    }

    public List<ResultDeliveryTicket> findAll() {
        return List.of();
    }

    public List<ResultDeliveryTicket> findByRecipientId(String recipientId) {
        return List.of();
    }
}
