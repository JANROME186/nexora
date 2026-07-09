package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.adapter.out.jdbc;

import java.math.BigDecimal;
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

import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteCodedValue;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteResultConstraint;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

@Repository
@Profile("local")
class JdbcAnalyteDefinitionRepository implements AnalyteDefinitionRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcAnalyteDefinitionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AnalyteDefinition save(AnalyteDefinition analyte) {
        jdbcTemplate.update("""
                insert into catalog.analyte_definitions
                    (analyte_id, tenant_id, laboratory_id, code, name_en, name_es, loinc_code, result_data_type,
                     measurement_unit, decimal_precision, status, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (analyte_id) do update set
                    code = excluded.code, name_en = excluded.name_en, name_es = excluded.name_es,
                    loinc_code = excluded.loinc_code, result_data_type = excluded.result_data_type,
                    measurement_unit = excluded.measurement_unit, decimal_precision = excluded.decimal_precision,
                    status = excluded.status, version = excluded.version, updated_at = excluded.updated_at
                """,
                analyte.analyteId(), analyte.tenantId(), analyte.laboratoryId(), analyte.code(),
                analyte.name().en(), analyte.name().es(), analyte.loincCode(), analyte.resultDataType(),
                analyte.measurementUnit(), analyte.decimalPrecision(), analyte.status(), analyte.version(),
                Timestamp.from(analyte.createdAt()), Timestamp.from(analyte.updatedAt()));
        return analyte;
    }

    @Override
    public Optional<AnalyteDefinition> findById(String analyteId) {
        return jdbcTemplate.query("""
                select analyte_id, tenant_id, laboratory_id, code, name_en, name_es, loinc_code, result_data_type,
                       measurement_unit, decimal_precision, status, version, created_at, updated_at
                from catalog.analyte_definitions
                where analyte_id = ?
                """, JdbcAnalyteDefinitionRepository::mapAnalyte, analyteId).stream().findFirst();
    }

    @Override
    public List<AnalyteDefinition> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select analyte_id, tenant_id, laboratory_id, code, name_en, name_es, loinc_code, result_data_type,
                       measurement_unit, decimal_precision, status, version, created_at, updated_at
                from catalog.analyte_definitions
                where laboratory_id = ?
                """, JdbcAnalyteDefinitionRepository::mapAnalyte, laboratoryId);
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludeAnalyteId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from catalog.analyte_definitions
                where laboratory_id = ? and code = ? and analyte_id <> ?
                """, Integer.class, laboratoryId, code, excludeAnalyteId == null ? "" : excludeAnalyteId);
        return count != null && count > 0;
    }

    @Override
    public void saveConstraint(AnalyteResultConstraint constraint) {
        jdbcTemplate.update("""
                insert into catalog.analyte_result_constraints (constraint_id, analyte_id, min_value, max_value)
                values (?, ?, ?, ?)
                on conflict (analyte_id) do update set
                    min_value = excluded.min_value, max_value = excluded.max_value
                """, constraint.constraintId(), constraint.analyteId(), constraint.minValue(), constraint.maxValue());
    }

    @Override
    public Optional<AnalyteResultConstraint> findConstraint(String analyteId) {
        return jdbcTemplate.query("""
                select constraint_id, analyte_id, min_value, max_value
                from catalog.analyte_result_constraints
                where analyte_id = ?
                """, (resultSet, rowNumber) -> new AnalyteResultConstraint(
                        resultSet.getString("constraint_id"),
                        resultSet.getString("analyte_id"),
                        resultSet.getBigDecimal("min_value"),
                        resultSet.getBigDecimal("max_value"),
                        List.of()),
                analyteId).stream().findFirst();
    }

    @Override
    @Transactional
    public void replaceCodedValues(String analyteId, List<AnalyteCodedValue> values) {
        jdbcTemplate.update("delete from catalog.analyte_coded_values where analyte_id = ?", analyteId);
        for (AnalyteCodedValue value : values) {
            jdbcTemplate.update("""
                    insert into catalog.analyte_coded_values (coded_value_id, analyte_id, code, display_en, display_es)
                    values (?, ?, ?, ?, ?)
                    """, value.codedValueId(), analyteId, value.code(), value.display().en(), value.display().es());
        }
    }

    @Override
    public List<AnalyteCodedValue> findCodedValues(String analyteId) {
        return jdbcTemplate.query("""
                select coded_value_id, analyte_id, code, display_en, display_es
                from catalog.analyte_coded_values
                where analyte_id = ?
                """, (resultSet, rowNumber) -> new AnalyteCodedValue(
                        resultSet.getString("coded_value_id"),
                        resultSet.getString("analyte_id"),
                        resultSet.getString("code"),
                        new LocalizedText(resultSet.getString("display_en"), resultSet.getString("display_es"))),
                analyteId);
    }

    private static AnalyteDefinition mapAnalyte(ResultSet resultSet, int rowNumber) throws SQLException {
        int precision = resultSet.getInt("decimal_precision");
        return new AnalyteDefinition(
                resultSet.getString("analyte_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("code"),
                new LocalizedText(resultSet.getString("name_en"), resultSet.getString("name_es")),
                resultSet.getString("loinc_code"),
                resultSet.getString("result_data_type"),
                resultSet.getString("measurement_unit"),
                resultSet.wasNull() ? null : precision,
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
