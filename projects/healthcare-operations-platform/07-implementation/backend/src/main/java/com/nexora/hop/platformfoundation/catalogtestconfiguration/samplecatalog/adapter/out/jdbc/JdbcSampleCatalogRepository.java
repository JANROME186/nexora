package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleCatalogRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleRequirement;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleType;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

@Repository
@Profile("local")
class JdbcSampleCatalogRepository implements SampleCatalogRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcSampleCatalogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SampleType saveSampleType(SampleType sampleType) {
        jdbcTemplate.update("""
                insert into catalog.sample_types
                    (sample_type_id, tenant_id, laboratory_id, code, name_en, name_es, matrix, status, version,
                     created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (sample_type_id) do update set
                    code = excluded.code, name_en = excluded.name_en, name_es = excluded.name_es,
                    matrix = excluded.matrix, status = excluded.status, version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                sampleType.sampleTypeId(), sampleType.tenantId(), sampleType.laboratoryId(), sampleType.code(),
                sampleType.name().en(), sampleType.name().es(), sampleType.matrix(), sampleType.status(),
                sampleType.version(), Timestamp.from(sampleType.createdAt()), Timestamp.from(sampleType.updatedAt()));
        return sampleType;
    }

    @Override
    public Optional<SampleType> findSampleTypeById(String sampleTypeId) {
        return jdbcTemplate.query("""
                select sample_type_id, tenant_id, laboratory_id, code, name_en, name_es, matrix, status, version,
                       created_at, updated_at
                from catalog.sample_types
                where sample_type_id = ?
                """, JdbcSampleCatalogRepository::mapSampleType, sampleTypeId).stream().findFirst();
    }

    @Override
    public List<SampleType> findSampleTypesByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select sample_type_id, tenant_id, laboratory_id, code, name_en, name_es, matrix, status, version,
                       created_at, updated_at
                from catalog.sample_types
                where laboratory_id = ?
                """, JdbcSampleCatalogRepository::mapSampleType, laboratoryId);
    }

    @Override
    public boolean existsSampleTypeByCode(String laboratoryId, String code, String excludeSampleTypeId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from catalog.sample_types
                where laboratory_id = ? and code = ? and sample_type_id <> ?
                """, Integer.class, laboratoryId, code, excludeSampleTypeId == null ? "" : excludeSampleTypeId);
        return count != null && count > 0;
    }

    @Override
    public SampleRequirement saveSampleRequirement(SampleRequirement requirement) {
        jdbcTemplate.update("""
                insert into catalog.sample_requirements
                    (requirement_id, tenant_id, laboratory_id, sample_type_ref_id, min_volume_ml, container_ref_id,
                     handling_instructions_en, handling_instructions_es, storage_temperature, status, version,
                     created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (requirement_id) do update set
                    sample_type_ref_id = excluded.sample_type_ref_id, min_volume_ml = excluded.min_volume_ml,
                    container_ref_id = excluded.container_ref_id,
                    handling_instructions_en = excluded.handling_instructions_en,
                    handling_instructions_es = excluded.handling_instructions_es,
                    storage_temperature = excluded.storage_temperature, status = excluded.status,
                    version = excluded.version, updated_at = excluded.updated_at
                """,
                requirement.requirementId(), requirement.tenantId(), requirement.laboratoryId(),
                requirement.sampleTypeRefId(), requirement.minVolumeMl(), requirement.containerRefId(),
                requirement.handlingInstructions() == null ? null : requirement.handlingInstructions().en(),
                requirement.handlingInstructions() == null ? null : requirement.handlingInstructions().es(),
                requirement.storageTemperature(), requirement.status(), requirement.version(),
                Timestamp.from(requirement.createdAt()), Timestamp.from(requirement.updatedAt()));
        return requirement;
    }

    @Override
    public Optional<SampleRequirement> findSampleRequirementById(String requirementId) {
        return jdbcTemplate.query("""
                select requirement_id, tenant_id, laboratory_id, sample_type_ref_id, min_volume_ml, container_ref_id,
                       handling_instructions_en, handling_instructions_es, storage_temperature, status, version,
                       created_at, updated_at
                from catalog.sample_requirements
                where requirement_id = ?
                """, JdbcSampleCatalogRepository::mapSampleRequirement, requirementId).stream().findFirst();
    }

    @Override
    public List<SampleRequirement> findSampleRequirementsByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select requirement_id, tenant_id, laboratory_id, sample_type_ref_id, min_volume_ml, container_ref_id,
                       handling_instructions_en, handling_instructions_es, storage_temperature, status, version,
                       created_at, updated_at
                from catalog.sample_requirements
                where laboratory_id = ?
                """, JdbcSampleCatalogRepository::mapSampleRequirement, laboratoryId);
    }

    private static SampleType mapSampleType(ResultSet resultSet, int rowNumber) throws SQLException {
        return new SampleType(
                resultSet.getString("sample_type_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("code"),
                new LocalizedText(resultSet.getString("name_en"), resultSet.getString("name_es")),
                resultSet.getString("matrix"),
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static SampleRequirement mapSampleRequirement(ResultSet resultSet, int rowNumber) throws SQLException {
        String handlingEn = resultSet.getString("handling_instructions_en");
        String handlingEs = resultSet.getString("handling_instructions_es");
        LocalizedText handlingInstructions = (handlingEn == null && handlingEs == null)
                ? null
                : new LocalizedText(handlingEn, handlingEs);
        return new SampleRequirement(
                resultSet.getString("requirement_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("sample_type_ref_id"),
                resultSet.getBigDecimal("min_volume_ml"),
                resultSet.getString("container_ref_id"),
                handlingInstructions,
                resultSet.getString("storage_temperature"),
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
