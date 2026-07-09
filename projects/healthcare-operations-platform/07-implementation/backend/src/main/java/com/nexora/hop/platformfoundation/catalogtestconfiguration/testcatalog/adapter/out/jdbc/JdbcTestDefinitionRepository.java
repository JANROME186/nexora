package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestAnalyteLink;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestSampleRequirementLink;

@Repository
@Profile("local")
class JdbcTestDefinitionRepository implements TestDefinitionRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcTestDefinitionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TestDefinition save(TestDefinition test) {
        jdbcTemplate.update("""
                insert into catalog.test_definitions
                    (test_definition_id, tenant_id, laboratory_id, code, name_en, name_es, methodology,
                     measurement_unit, result_type, turnaround_time_hours, status, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (test_definition_id) do update set
                    code = excluded.code, name_en = excluded.name_en, name_es = excluded.name_es,
                    methodology = excluded.methodology, measurement_unit = excluded.measurement_unit,
                    result_type = excluded.result_type, turnaround_time_hours = excluded.turnaround_time_hours,
                    status = excluded.status, version = excluded.version, updated_at = excluded.updated_at
                """,
                test.testDefinitionId(), test.tenantId(), test.laboratoryId(), test.code(),
                test.name().en(), test.name().es(), test.methodology(), test.measurementUnit(), test.resultType(),
                test.turnaroundTimeHours(), test.status(), test.version(),
                Timestamp.from(test.createdAt()), Timestamp.from(test.updatedAt()));
        return test;
    }

    @Override
    public Optional<TestDefinition> findById(String testDefinitionId) {
        return jdbcTemplate.query("""
                select test_definition_id, tenant_id, laboratory_id, code, name_en, name_es, methodology,
                       measurement_unit, result_type, turnaround_time_hours, status, version, created_at, updated_at
                from catalog.test_definitions
                where test_definition_id = ?
                """, JdbcTestDefinitionRepository::mapTest, testDefinitionId).stream().findFirst();
    }

    @Override
    public List<TestDefinition> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select test_definition_id, tenant_id, laboratory_id, code, name_en, name_es, methodology,
                       measurement_unit, result_type, turnaround_time_hours, status, version, created_at, updated_at
                from catalog.test_definitions
                where laboratory_id = ?
                """, JdbcTestDefinitionRepository::mapTest, laboratoryId);
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludeTestDefinitionId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from catalog.test_definitions
                where laboratory_id = ? and code = ? and test_definition_id <> ?
                """, Integer.class, laboratoryId, code, excludeTestDefinitionId == null ? "" : excludeTestDefinitionId);
        return count != null && count > 0;
    }

    @Override
    @Transactional
    public void replaceAnalyteLinks(String testDefinitionId, List<TestAnalyteLink> links) {
        jdbcTemplate.update("delete from catalog.test_analyte_links where test_definition_id = ?", testDefinitionId);
        for (TestAnalyteLink link : links) {
            jdbcTemplate.update("""
                    insert into catalog.test_analyte_links (link_id, test_definition_id, analyte_ref_id, display_order)
                    values (?, ?, ?, ?)
                    """, link.linkId(), testDefinitionId, link.analyteRefId(), link.displayOrder());
        }
    }

    @Override
    public List<TestAnalyteLink> findAnalyteLinks(String testDefinitionId) {
        return jdbcTemplate.query("""
                select link_id, test_definition_id, analyte_ref_id, display_order
                from catalog.test_analyte_links
                where test_definition_id = ?
                """, (resultSet, rowNumber) -> {
            int displayOrder = resultSet.getInt("display_order");
            return new TestAnalyteLink(
                    resultSet.getString("link_id"),
                    resultSet.getString("test_definition_id"),
                    resultSet.getString("analyte_ref_id"),
                    resultSet.wasNull() ? null : displayOrder);
        }, testDefinitionId);
    }

    @Override
    @Transactional
    public void replaceSampleRequirementLinks(String testDefinitionId, List<TestSampleRequirementLink> links) {
        jdbcTemplate.update(
                "delete from catalog.test_sample_requirement_links where test_definition_id = ?", testDefinitionId);
        for (TestSampleRequirementLink link : links) {
            jdbcTemplate.update("""
                    insert into catalog.test_sample_requirement_links
                        (link_id, test_definition_id, sample_requirement_ref_id)
                    values (?, ?, ?)
                    """, link.linkId(), testDefinitionId, link.sampleRequirementRefId());
        }
    }

    @Override
    public List<TestSampleRequirementLink> findSampleRequirementLinks(String testDefinitionId) {
        return jdbcTemplate.query("""
                select link_id, test_definition_id, sample_requirement_ref_id
                from catalog.test_sample_requirement_links
                where test_definition_id = ?
                """, (resultSet, rowNumber) -> new TestSampleRequirementLink(
                        resultSet.getString("link_id"),
                        resultSet.getString("test_definition_id"),
                        resultSet.getString("sample_requirement_ref_id")),
                testDefinitionId);
    }

    private static TestDefinition mapTest(ResultSet resultSet, int rowNumber) throws SQLException {
        int turnaround = resultSet.getInt("turnaround_time_hours");
        return new TestDefinition(
                resultSet.getString("test_definition_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("code"),
                new LocalizedText(resultSet.getString("name_en"), resultSet.getString("name_es")),
                resultSet.getString("methodology"),
                resultSet.getString("measurement_unit"),
                resultSet.getString("result_type"),
                resultSet.wasNull() ? null : turnaround,
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
