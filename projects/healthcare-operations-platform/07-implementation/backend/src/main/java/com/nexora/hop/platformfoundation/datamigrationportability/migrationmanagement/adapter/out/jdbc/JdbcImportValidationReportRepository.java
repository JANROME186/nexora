package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportValidationReport;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportValidationReportRepository;
import com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcImportValidationReportRepository implements ImportValidationReportRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcImportValidationReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ImportValidationReport save(ImportValidationReport report) {
        jdbcTemplate.update("""
                insert into data_migration_portability.import_validation_reports
                    (report_id, import_batch_id, structural_errors_text, row_level_errors_text,
                     row_level_warnings_text, validation_categories_text, passed,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                report.reportId(), report.importBatchId(), DelimitedTextCodec.joinList(report.structuralErrors()),
                DelimitedTextCodec.joinList(report.rowLevelErrors()), DelimitedTextCodec.joinList(report.rowLevelWarnings()),
                DelimitedTextCodec.joinList(report.validationCategoriesEvaluated()), report.passed(),
                report.audit().createdBy(), Timestamp.valueOf(report.audit().createdAt()), report.audit().updatedBy(),
                Timestamp.valueOf(report.audit().updatedAt()));
        return report;
    }

    @Override
    public Optional<ImportValidationReport> findLatestByImportBatchId(String importBatchId) {
        return jdbcTemplate.query("""
                select report_id, import_batch_id, structural_errors_text, row_level_errors_text,
                       row_level_warnings_text, validation_categories_text, passed,
                       created_by, created_at, updated_by, updated_at
                from data_migration_portability.import_validation_reports
                where import_batch_id = ?
                order by created_at desc
                """, JdbcImportValidationReportRepository::map, importBatchId).stream().findFirst();
    }

    private static ImportValidationReport map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new ImportValidationReport(
                resultSet.getString("report_id"),
                resultSet.getString("import_batch_id"),
                DelimitedTextCodec.splitList(resultSet.getString("structural_errors_text")),
                DelimitedTextCodec.splitList(resultSet.getString("row_level_errors_text")),
                DelimitedTextCodec.splitList(resultSet.getString("row_level_warnings_text")),
                DelimitedTextCodec.splitList(resultSet.getString("validation_categories_text")),
                resultSet.getBoolean("passed"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
