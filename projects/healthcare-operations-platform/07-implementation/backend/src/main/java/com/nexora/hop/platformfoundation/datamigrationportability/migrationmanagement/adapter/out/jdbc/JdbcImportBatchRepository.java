package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatch;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatchRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationManifest;
import com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcImportBatchRepository implements ImportBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcImportBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ImportBatch save(ImportBatch batch) {
        MigrationManifest manifest = batch.manifest();
        jdbcTemplate.update("""
                insert into data_migration_portability.import_batches
                    (import_batch_id, migration_job_id, source_system_name, source_system_version,
                     exporting_organization, export_datetime, export_timezone, exported_by, contact_email,
                     manifest_files_text, manifest_entity_counts_text, checksum_algorithm,
                     manifest_checksums_text, declared_formats_text, declared_encoding,
                     stored_package_reference, entity_counts_text, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (import_batch_id) do update set
                    stored_package_reference = excluded.stored_package_reference,
                    entity_counts_text = excluded.entity_counts_text, updated_by = excluded.updated_by,
                    updated_at = excluded.updated_at
                """,
                batch.importBatchId(), batch.migrationJobId(), manifest.sourceSystemName(),
                manifest.sourceSystemVersion(), manifest.exportingOrganization(), Timestamp.from(manifest.exportDatetime()),
                manifest.exportTimezone(), manifest.exportedBy(), manifest.contactEmail(),
                DelimitedTextCodec.joinList(manifest.files()), DelimitedTextCodec.joinIntMap(manifest.entityCounts()),
                manifest.checksumAlgorithm(), DelimitedTextCodec.joinStringMap(manifest.checksums()),
                DelimitedTextCodec.joinList(manifest.declaredFormats()), manifest.declaredEncoding(),
                batch.storedPackageReference(), DelimitedTextCodec.joinIntMap(batch.entityCounts()),
                batch.audit().createdBy(), Timestamp.valueOf(batch.audit().createdAt()), batch.audit().updatedBy(),
                Timestamp.valueOf(batch.audit().updatedAt()));
        return batch;
    }

    @Override
    public Optional<ImportBatch> findById(String importBatchId) {
        return jdbcTemplate.query(SELECT_SQL + " where import_batch_id = ?", JdbcImportBatchRepository::map,
                importBatchId).stream().findFirst();
    }

    @Override
    public List<ImportBatch> findByMigrationJobId(String migrationJobId) {
        return jdbcTemplate.query(SELECT_SQL + " where migration_job_id = ?", JdbcImportBatchRepository::map,
                migrationJobId);
    }

    private static final String SELECT_SQL = """
            select import_batch_id, migration_job_id, source_system_name, source_system_version,
                   exporting_organization, export_datetime, export_timezone, exported_by, contact_email,
                   manifest_files_text, manifest_entity_counts_text, checksum_algorithm,
                   manifest_checksums_text, declared_formats_text, declared_encoding,
                   stored_package_reference, entity_counts_text, created_by, created_at, updated_by, updated_at
            from data_migration_portability.import_batches
            """;

    private static ImportBatch map(ResultSet resultSet, int rowNumber) throws SQLException {
        MigrationManifest manifest = new MigrationManifest(
                resultSet.getString("source_system_name"),
                resultSet.getString("source_system_version"),
                resultSet.getString("exporting_organization"),
                resultSet.getTimestamp("export_datetime").toInstant(),
                resultSet.getString("export_timezone"),
                resultSet.getString("exported_by"),
                resultSet.getString("contact_email"),
                DelimitedTextCodec.splitList(resultSet.getString("manifest_files_text")),
                DelimitedTextCodec.splitIntMap(resultSet.getString("manifest_entity_counts_text")),
                resultSet.getString("checksum_algorithm"),
                DelimitedTextCodec.splitStringMap(resultSet.getString("manifest_checksums_text")),
                DelimitedTextCodec.splitList(resultSet.getString("declared_formats_text")),
                resultSet.getString("declared_encoding"));
        return new ImportBatch(
                resultSet.getString("import_batch_id"),
                resultSet.getString("migration_job_id"),
                manifest,
                resultSet.getString("stored_package_reference"),
                DelimitedTextCodec.splitIntMap(resultSet.getString("entity_counts_text")),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
