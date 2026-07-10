package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonMergeCoordination;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonMergeCoordinationRepository;

@Repository
@Profile("local")
class JdbcPersonMergeCoordinationRepository implements PersonMergeCoordinationRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcPersonMergeCoordinationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PersonMergeCoordination save(PersonMergeCoordination coordination) {
        jdbcTemplate.update("""
                insert into people.person_merge_coordinations (
                    coordination_id, tenant_id, source_kind, source_record_id,
                    target_kind, target_record_id, status, patient_merge_applied,
                    created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (coordination_id) do update set
                    status = excluded.status,
                    patient_merge_applied = excluded.patient_merge_applied,
                    updated_at = excluded.updated_at
                """,
                coordination.coordinationId(), coordination.tenantId(), coordination.sourceKind(),
                coordination.sourceRecordId(), coordination.targetKind(), coordination.targetRecordId(),
                coordination.status(), coordination.patientMergeApplied(),
                Timestamp.from(coordination.createdAt()), Timestamp.from(coordination.updatedAt()));
        return coordination;
    }

    @Override
    public Optional<PersonMergeCoordination> findById(String coordinationId) {
        return jdbcTemplate.query("select * from people.person_merge_coordinations where coordination_id = ?",
                JdbcPersonMergeCoordinationRepository::map, coordinationId).stream().findFirst();
    }

    private static PersonMergeCoordination map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new PersonMergeCoordination(
                resultSet.getString("coordination_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("source_kind"),
                resultSet.getString("source_record_id"),
                resultSet.getString("target_kind"),
                resultSet.getString("target_record_id"),
                resultSet.getString("status"),
                resultSet.getBoolean("patient_merge_applied"),
                resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getTimestamp("updated_at").toInstant());
    }
}
