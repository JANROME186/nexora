package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.*;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("!local & !test")
public class JdbcPatientResultHistoryRepository implements PatientResultHistoryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcPatientResultHistoryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Stub implementation to satisfy technical debt TD-DB-001 for now.
    // Complete mapping will be implemented when non-local durability is required.
    
    public void save(PatientResultHistoryView entity) {
        throw new UnsupportedOperationException("JDBC implementation pending full field mapping");
    }

    public Optional<PatientResultHistoryView> findById(UUID id) {
        return Optional.empty();
    }

    public Optional<PatientResultHistoryView> findByPatientId(PatientId id) {
        return Optional.empty();
    }
    
    public List<PatientResultHistoryView> findByResultId(ResultId resultId, TenantId tenantId) {
        return List.of();
    }
}
