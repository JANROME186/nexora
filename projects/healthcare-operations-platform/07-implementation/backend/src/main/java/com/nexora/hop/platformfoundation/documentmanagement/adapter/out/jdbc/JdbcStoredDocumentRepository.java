package com.nexora.hop.platformfoundation.documentmanagement.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.documentmanagement.domain.*;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("!local & !test")
public class JdbcStoredDocumentRepository implements StoredDocumentRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcStoredDocumentRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Stub implementation to satisfy technical debt TD-DB-001 for now.
    // Complete mapping will be implemented when non-local durability is required.
    
    public StoredDocument save(StoredDocument entity) {
        throw new UnsupportedOperationException("JDBC implementation pending full field mapping");
    }

    public Optional<StoredDocument> findById(UUID id) {
        return Optional.empty();
    }

    public Optional<StoredDocument> findByPatientId(PatientId id) {
        return Optional.empty();
    }
    
    public List<StoredDocument> findByResultId(ResultId resultId, TenantId tenantId) {
        return List.of();
    }
}
