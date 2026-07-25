package com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReport;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReportRepository;
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
public class JdbcRadiologyReportRepository implements RadiologyReportRepository {

    private static final String SELECT_SQL = """
            select report_id, tenant_id, study_id, radiologist_id, findings_text,
                   impression_text, report_status, signed_at, digital_signature_hash,
                   created_by, created_at, updated_by, updated_at
            from imaging_operations.radiology_reports
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcRadiologyReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RadiologyReport save(RadiologyReport report) {
        jdbcTemplate.update("""
                insert into imaging_operations.radiology_reports
                    (report_id, tenant_id, study_id, radiologist_id, findings_text,
                     impression_text, report_status, signed_at, digital_signature_hash,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (report_id) do update set
                    findings_text = excluded.findings_text,
                    impression_text = excluded.impression_text,
                    report_status = excluded.report_status,
                    signed_at = excluded.signed_at,
                    digital_signature_hash = excluded.digital_signature_hash,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                report.reportId(), report.tenantId(), report.studyId(), report.radiologistId(),
                report.findingsText(), report.impressionText(), report.reportStatus(),
                report.signedAt() != null ? Timestamp.from(report.signedAt()) : null,
                report.digitalSignatureHash(), report.createdBy(), Timestamp.from(report.createdAt()),
                report.updatedBy(), Timestamp.from(report.updatedAt()));
        return report;
    }

    @Override
    public Optional<RadiologyReport> findById(String tenantId, String reportId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and report_id = ?",
                JdbcRadiologyReportRepository::map, tenantId, reportId).stream().findFirst();
    }

    @Override
    public List<RadiologyReport> findByStudyId(String tenantId, String studyId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and study_id = ?",
                JdbcRadiologyReportRepository::map, tenantId, studyId);
    }

    private static RadiologyReport map(ResultSet rs, int rowNum) throws SQLException {
        Timestamp signedAtTs = rs.getTimestamp("signed_at");
        return new RadiologyReport(
                rs.getString("report_id"),
                rs.getString("tenant_id"),
                rs.getString("study_id"),
                rs.getString("radiologist_id"),
                rs.getString("findings_text"),
                rs.getString("impression_text"),
                rs.getString("report_status"),
                signedAtTs != null ? signedAtTs.toInstant() : null,
                rs.getString("digital_signature_hash"),
                rs.getString("created_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("updated_by"),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
