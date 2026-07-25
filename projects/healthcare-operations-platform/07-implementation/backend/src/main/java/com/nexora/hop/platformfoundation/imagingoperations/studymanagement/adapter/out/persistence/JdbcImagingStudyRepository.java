package com.nexora.hop.platformfoundation.imagingoperations.studymanagement.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudy;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudyRepository;
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
public class JdbcImagingStudyRepository implements ImagingStudyRepository {

    private static final String SELECT_SQL = """
            select study_id, tenant_id, accession_number, patient_id, modality,
                   study_description, study_status, series_count, instance_count, study_date,
                   created_by, created_at, updated_by, updated_at
            from imaging_operations.imaging_studies
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcImagingStudyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ImagingStudy save(ImagingStudy study) {
        jdbcTemplate.update("""
                insert into imaging_operations.imaging_studies
                    (study_id, tenant_id, accession_number, patient_id, modality,
                     study_description, study_status, series_count, instance_count, study_date,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (study_id) do update set
                    study_description = excluded.study_description,
                    study_status = excluded.study_status,
                    series_count = excluded.series_count,
                    instance_count = excluded.instance_count,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                study.studyId(), study.tenantId(), study.accessionNumber(), study.patientId(),
                study.modality(), study.studyDescription(), study.studyStatus(), study.seriesCount(),
                study.instanceCount(), Timestamp.from(study.studyDate()), study.createdBy(),
                Timestamp.from(study.createdAt()), study.updatedBy(), Timestamp.from(study.updatedAt()));
        return study;
    }

    @Override
    public Optional<ImagingStudy> findById(String tenantId, String studyId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and study_id = ?",
                JdbcImagingStudyRepository::map, tenantId, studyId).stream().findFirst();
    }

    @Override
    public Optional<ImagingStudy> findByAccessionNumber(String tenantId, String accessionNumber) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and lower(accession_number) = lower(?)",
                JdbcImagingStudyRepository::map, tenantId, accessionNumber).stream().findFirst();
    }

    @Override
    public List<ImagingStudy> findByTenantAndPatient(String tenantId, String patientId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and patient_id = ?",
                JdbcImagingStudyRepository::map, tenantId, patientId);
    }

    private static ImagingStudy map(ResultSet rs, int rowNum) throws SQLException {
        return new ImagingStudy(
                rs.getString("study_id"),
                rs.getString("tenant_id"),
                rs.getString("accession_number"),
                rs.getString("patient_id"),
                rs.getString("modality"),
                rs.getString("study_description"),
                rs.getString("study_status"),
                rs.getInt("series_count"),
                rs.getInt("instance_count"),
                rs.getTimestamp("study_date").toInstant(),
                rs.getString("created_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("updated_by"),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
