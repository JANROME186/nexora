package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MappingTemplate;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MappingTemplateRepository;
import com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcMappingTemplateRepository implements MappingTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcMappingTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MappingTemplate save(MappingTemplate template) {
        jdbcTemplate.update("""
                insert into data_migration_portability.mapping_templates
                    (mapping_template_id, import_batch_id, field_mappings_text, code_dictionaries_text,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                template.mappingTemplateId(), template.importBatchId(),
                DelimitedTextCodec.joinStringMap(template.fieldMappings()),
                DelimitedTextCodec.joinStringMap(template.codeDictionaries()), template.audit().createdBy(),
                Timestamp.valueOf(template.audit().createdAt()), template.audit().updatedBy(),
                Timestamp.valueOf(template.audit().updatedAt()));
        return template;
    }

    @Override
    public Optional<MappingTemplate> findByImportBatchId(String importBatchId) {
        return jdbcTemplate.query("""
                select mapping_template_id, import_batch_id, field_mappings_text, code_dictionaries_text,
                       created_by, created_at, updated_by, updated_at
                from data_migration_portability.mapping_templates
                where import_batch_id = ?
                order by created_at desc
                """, JdbcMappingTemplateRepository::map, importBatchId).stream().findFirst();
    }

    private static MappingTemplate map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new MappingTemplate(
                resultSet.getString("mapping_template_id"),
                resultSet.getString("import_batch_id"),
                DelimitedTextCodec.splitStringMap(resultSet.getString("field_mappings_text")),
                DelimitedTextCodec.splitStringMap(resultSet.getString("code_dictionaries_text")),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
