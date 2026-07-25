package com.nexora.hop.platformfoundation.notificationmanagement.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.notificationmanagement.domain.*;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("!local & !test")
public class JdbcNotificationRequestRepository implements NotificationRequestRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcNotificationRequestRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Stub implementation to satisfy technical debt TD-DB-001 for now.
    // Complete mapping will be implemented when non-local durability is required.

    public NotificationRequest save(NotificationRequest entity) {
        throw new UnsupportedOperationException("JDBC implementation pending full field mapping");
    }

    public Optional<NotificationRequest> findById(UUID id) {
        return Optional.empty();
    }

    public Optional<NotificationRequest> findByPatientId(PatientId id) {
        return Optional.empty();
    }

    public List<NotificationRequest> findByResultId(ResultId resultId, TenantId tenantId) {
        return List.of();
    }

    public List<NotificationRequest> findAll() {
        return List.of();
    }

    public List<NotificationRequest> findByRecipientId(String recipientId) {
        return List.of();
    }
}
