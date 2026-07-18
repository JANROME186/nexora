package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ReconciliationReport;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ReconciliationReportRepository;
import com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcReconciliationReportRepository implements ReconciliationReportRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcReconciliationReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReconciliationReport save(ReconciliationReport report) {
        jdbcTemplate.update("""
                insert into data_migration_portability.reconciliation_reports
                    (reconciliation_report_id, migration_job_id, phase, imported_counts_text,
                     rejected_counts_text, skipped_counts_text, warning_counts_text,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                report.reconciliationReportId(), report.migrationJobId(), report.phase(),
                DelimitedTextCodec.joinIntMap(report.importedCounts()),
                DelimitedTextCodec.joinIntMap(report.rejectedCounts()),
                DelimitedTextCodec.joinIntMap(report.skippedCounts()),
                DelimitedTextCodec.joinIntMap(report.warningCounts()), report.audit().createdBy(),
                Timestamp.valueOf(report.audit().createdAt()), report.audit().updatedBy(),
                Timestamp.valueOf(report.audit().updatedAt()));
        return report;
    }

    @Override
    public List<ReconciliationReport> findByMigrationJobId(String migrationJobId) {
        return jdbcTemplate.query("""
                select reconciliation_report_id, migration_job_id, phase, imported_counts_text,
                       rejected_counts_text, skipped_counts_text, warning_counts_text,
                       created_by, created_at, updated_by, updated_at
                from data_migration_portability.reconciliation_reports
                where migration_job_id = ?
                order by created_at
                """, JdbcReconciliationReportRepository::map, migrationJobId);
    }

    private static ReconciliationReport map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new ReconciliationReport(
                resultSet.getString("reconciliation_report_id"),
                resultSet.getString("migration_job_id"),
                resultSet.getString("phase"),
                DelimitedTextCodec.splitIntMap(resultSet.getString("imported_counts_text")),
                DelimitedTextCodec.splitIntMap(resultSet.getString("rejected_counts_text")),
                DelimitedTextCodec.splitIntMap(resultSet.getString("skipped_counts_text")),
                DelimitedTextCodec.splitIntMap(resultSet.getString("warning_counts_text")),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
