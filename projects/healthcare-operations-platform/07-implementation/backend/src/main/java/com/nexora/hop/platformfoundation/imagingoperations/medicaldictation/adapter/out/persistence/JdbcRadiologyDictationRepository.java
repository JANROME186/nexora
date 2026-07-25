package com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain.RadiologyDictation;
import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain.RadiologyDictationRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
public class JdbcRadiologyDictationRepository implements RadiologyDictationRepository {

    private static final String SELECT_SQL = """
            select dictation_id, tenant_id, study_id, radiologist_id, dictation_text,
                   audio_reference_url, dictation_status, created_by, created_at, updated_by, updated_at
            from imaging_operations.radiology_dictations
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcRadiologyDictationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RadiologyDictation save(RadiologyDictation dictation) {
        jdbcTemplate.update("""
                insert into imaging_operations.radiology_dictations
                    (dictation_id, tenant_id, study_id, radiologist_id, dictation_text,
                     audio_reference_url, dictation_status, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (dictation_id) do update set
                    dictation_text = excluded.dictation_text,
                    audio_reference_url = excluded.audio_reference_url,
                    dictation_status = excluded.dictation_status,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                dictation.dictationId(), dictation.tenantId(), dictation.studyId(), dictation.radiologistId(),
                dictation.dictationText(), dictation.audioReferenceUrl(), dictation.dictationStatus(),
                dictation.createdBy(), Timestamp.from(dictation.createdAt()), dictation.updatedBy(),
                Timestamp.from(dictation.updatedAt()));
        return dictation;
    }

    @Override
    public Optional<RadiologyDictation> findById(String tenantId, String dictationId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and dictation_id = ?",
                JdbcRadiologyDictationRepository::map, tenantId, dictationId).stream().findFirst();
    }

    @Override
    public List<RadiologyDictation> findByStudyId(String tenantId, String studyId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and study_id = ?",
                JdbcRadiologyDictationRepository::map, tenantId, studyId);
    }

    private static RadiologyDictation map(ResultSet rs, int rowNum) throws SQLException {
        return new RadiologyDictation(
                rs.getString("dictation_id"),
                rs.getString("tenant_id"),
                rs.getString("study_id"),
                rs.getString("radiologist_id"),
                rs.getString("dictation_text"),
                rs.getString("audio_reference_url"),
                rs.getString("dictation_status"),
                rs.getString("created_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("updated_by"),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
