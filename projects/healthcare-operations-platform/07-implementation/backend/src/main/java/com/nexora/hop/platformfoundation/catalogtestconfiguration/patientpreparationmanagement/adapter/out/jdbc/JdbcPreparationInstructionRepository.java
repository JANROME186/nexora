package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationAssignment;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstruction;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstructionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

@Repository
@Profile("local")
class JdbcPreparationInstructionRepository implements PreparationInstructionRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcPreparationInstructionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PreparationInstruction save(PreparationInstruction preparation) {
        jdbcTemplate.update("""
                insert into catalog.preparation_instructions
                    (preparation_id, tenant_id, laboratory_id, code, title_en, title_es, instruction_text_en,
                     instruction_text_es, category, duration_hours, status, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (preparation_id) do update set
                    code = excluded.code, title_en = excluded.title_en, title_es = excluded.title_es,
                    instruction_text_en = excluded.instruction_text_en, instruction_text_es = excluded.instruction_text_es,
                    category = excluded.category, duration_hours = excluded.duration_hours,
                    status = excluded.status, version = excluded.version, updated_at = excluded.updated_at
                """,
                preparation.preparationId(), preparation.tenantId(), preparation.laboratoryId(), preparation.code(),
                preparation.title().en(), preparation.title().es(), preparation.instructionText().en(),
                preparation.instructionText().es(), preparation.category(), preparation.durationHours(),
                preparation.status(), preparation.version(),
                Timestamp.from(preparation.createdAt()), Timestamp.from(preparation.updatedAt()));
        return preparation;
    }

    @Override
    public Optional<PreparationInstruction> findById(String preparationId) {
        return jdbcTemplate.query("""
                select preparation_id, tenant_id, laboratory_id, code, title_en, title_es, instruction_text_en,
                       instruction_text_es, category, duration_hours, status, version, created_at, updated_at
                from catalog.preparation_instructions
                where preparation_id = ?
                """, JdbcPreparationInstructionRepository::mapPreparation, preparationId).stream().findFirst();
    }

    @Override
    public List<PreparationInstruction> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select preparation_id, tenant_id, laboratory_id, code, title_en, title_es, instruction_text_en,
                       instruction_text_es, category, duration_hours, status, version, created_at, updated_at
                from catalog.preparation_instructions
                where laboratory_id = ?
                """, JdbcPreparationInstructionRepository::mapPreparation, laboratoryId);
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludePreparationId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from catalog.preparation_instructions
                where laboratory_id = ? and code = ? and preparation_id <> ?
                """, Integer.class, laboratoryId, code, excludePreparationId == null ? "" : excludePreparationId);
        return count != null && count > 0;
    }

    @Override
    public PreparationAssignment saveAssignment(PreparationAssignment assignment) {
        jdbcTemplate.update("""
                insert into catalog.preparation_assignments (assignment_id, preparation_id, target_type, target_ref_id)
                values (?, ?, ?, ?)
                """, assignment.assignmentId(), assignment.preparationId(), assignment.targetType(), assignment.targetRefId());
        return assignment;
    }

    @Override
    public List<PreparationAssignment> findAssignments(String preparationId) {
        return jdbcTemplate.query("""
                select assignment_id, preparation_id, target_type, target_ref_id
                from catalog.preparation_assignments
                where preparation_id = ?
                """, (resultSet, rowNumber) -> new PreparationAssignment(
                        resultSet.getString("assignment_id"),
                        resultSet.getString("preparation_id"),
                        resultSet.getString("target_type"),
                        resultSet.getString("target_ref_id")),
                preparationId);
    }

    @Override
    public List<PreparationAssignment> findAssignmentsByTarget(String targetType, String targetRefId) {
        return jdbcTemplate.query("""
                select assignment_id, preparation_id, target_type, target_ref_id
                from catalog.preparation_assignments
                where target_type = ? and target_ref_id = ?
                """, (resultSet, rowNumber) -> new PreparationAssignment(
                        resultSet.getString("assignment_id"),
                        resultSet.getString("preparation_id"),
                        resultSet.getString("target_type"),
                        resultSet.getString("target_ref_id")),
                targetType, targetRefId);
    }

    private static PreparationInstruction mapPreparation(ResultSet resultSet, int rowNumber) throws SQLException {
        int duration = resultSet.getInt("duration_hours");
        return new PreparationInstruction(
                resultSet.getString("preparation_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("code"),
                new LocalizedText(resultSet.getString("title_en"), resultSet.getString("title_es")),
                new LocalizedText(resultSet.getString("instruction_text_en"), resultSet.getString("instruction_text_es")),
                resultSet.getString("category"),
                resultSet.wasNull() ? null : duration,
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
