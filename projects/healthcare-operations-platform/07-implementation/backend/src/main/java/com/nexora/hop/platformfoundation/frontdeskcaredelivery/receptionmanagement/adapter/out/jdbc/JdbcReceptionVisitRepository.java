package com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain.ReceptionVisit;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain.ReceptionVisitRepository;

@Repository
@Profile("local")
class JdbcReceptionVisitRepository implements ReceptionVisitRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcReceptionVisitRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReceptionVisit save(ReceptionVisit visit) {
        jdbcTemplate.update("""
                insert into care_delivery.reception_visits (
                    visit_id, tenant_id, laboratory_id, branch_id, patient_id, linked_appointment_id,
                    intake_channel, identity_confirmed, identity_confirmation_method, queue_status,
                    priority, actor_id, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (visit_id) do update set
                    identity_confirmed = excluded.identity_confirmed,
                    identity_confirmation_method = excluded.identity_confirmation_method,
                    queue_status = excluded.queue_status,
                    priority = excluded.priority,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                visit.visitId(), visit.tenantId(), visit.laboratoryId(), visit.branchId(), visit.patientId(),
                visit.linkedAppointmentId(), visit.intakeChannel(), visit.identityConfirmed(),
                visit.identityConfirmationMethod(), visit.queueStatus(), visit.priority(), visit.actorId(),
                visit.version(), Timestamp.from(visit.createdAt()), Timestamp.from(visit.updatedAt()));
        return visit;
    }

    @Override
    public Optional<ReceptionVisit> findById(String visitId) {
        return jdbcTemplate.query("select * from care_delivery.reception_visits where visit_id = ?",
                JdbcReceptionVisitRepository::map, visitId).stream().findFirst();
    }

    @Override
    public List<ReceptionVisit> findByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from care_delivery.reception_visits where tenant_id = ?",
                JdbcReceptionVisitRepository::map, tenantId);
    }

    private static ReceptionVisit map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new ReceptionVisit(
                resultSet.getString("visit_id"), resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"), resultSet.getString("branch_id"),
                resultSet.getString("patient_id"), resultSet.getString("linked_appointment_id"),
                resultSet.getString("intake_channel"), resultSet.getBoolean("identity_confirmed"),
                resultSet.getString("identity_confirmation_method"), resultSet.getString("queue_status"),
                resultSet.getString("priority"), resultSet.getString("actor_id"), resultSet.getInt("version"),
                resultSet.getTimestamp("created_at").toInstant(), resultSet.getTimestamp("updated_at").toInstant());
    }
}
