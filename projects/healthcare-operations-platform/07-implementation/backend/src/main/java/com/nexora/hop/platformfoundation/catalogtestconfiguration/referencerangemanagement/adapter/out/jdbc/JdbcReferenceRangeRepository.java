package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.adapter.out.jdbc;

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

import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRange;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeSegment;

@Repository
@Profile("local")
class JdbcReferenceRangeRepository implements ReferenceRangeRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcReferenceRangeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReferenceRange save(ReferenceRange range) {
        jdbcTemplate.update("""
                insert into catalog.reference_ranges
                    (range_id, tenant_id, laboratory_id, analyte_ref_id, version, status, effective_from,
                     effective_to, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (range_id) do update set
                    status = excluded.status, version = excluded.version, effective_from = excluded.effective_from,
                    effective_to = excluded.effective_to, updated_at = excluded.updated_at
                """,
                range.rangeId(), range.tenantId(), range.laboratoryId(), range.analyteRefId(), range.version(),
                range.status(), range.effectiveFrom(), range.effectiveTo(),
                Timestamp.from(range.createdAt()), Timestamp.from(range.updatedAt()));
        return range;
    }

    @Override
    public Optional<ReferenceRange> findById(String rangeId) {
        return jdbcTemplate.query("""
                select range_id, tenant_id, laboratory_id, analyte_ref_id, version, status, effective_from,
                       effective_to, created_at, updated_at
                from catalog.reference_ranges
                where range_id = ?
                """, JdbcReferenceRangeRepository::mapRange, rangeId).stream().findFirst();
    }

    @Override
    public List<ReferenceRange> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select range_id, tenant_id, laboratory_id, analyte_ref_id, version, status, effective_from,
                       effective_to, created_at, updated_at
                from catalog.reference_ranges
                where laboratory_id = ?
                """, JdbcReferenceRangeRepository::mapRange, laboratoryId);
    }

    @Override
    public List<ReferenceRange> findByAnalyteRefId(String analyteRefId) {
        return jdbcTemplate.query("""
                select range_id, tenant_id, laboratory_id, analyte_ref_id, version, status, effective_from,
                       effective_to, created_at, updated_at
                from catalog.reference_ranges
                where analyte_ref_id = ?
                """, JdbcReferenceRangeRepository::mapRange, analyteRefId);
    }

    @Override
    @Transactional
    public void replaceSegments(String rangeId, List<ReferenceRangeSegment> segments) {
        jdbcTemplate.update("delete from catalog.reference_range_segments where range_id = ?", rangeId);
        for (ReferenceRangeSegment segment : segments) {
            jdbcTemplate.update("""
                    insert into catalog.reference_range_segments
                        (segment_id, range_id, sex, age_min_days, age_max_days, condition, normal_low, normal_high,
                         critical_low, critical_high, unit)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    segment.segmentId(), rangeId, segment.sex(), segment.ageMinDays(), segment.ageMaxDays(),
                    segment.condition(), segment.normalLow(), segment.normalHigh(), segment.criticalLow(),
                    segment.criticalHigh(), segment.unit());
        }
    }

    @Override
    public List<ReferenceRangeSegment> findSegments(String rangeId) {
        return jdbcTemplate.query("""
                select segment_id, range_id, sex, age_min_days, age_max_days, condition, normal_low, normal_high,
                       critical_low, critical_high, unit
                from catalog.reference_range_segments
                where range_id = ?
                """, JdbcReferenceRangeRepository::mapSegment, rangeId);
    }

    private static ReferenceRange mapRange(ResultSet resultSet, int rowNumber) throws SQLException {
        return new ReferenceRange(
                resultSet.getString("range_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("analyte_ref_id"),
                resultSet.getInt("version"),
                resultSet.getString("status"),
                resultSet.getObject("effective_from", java.time.LocalDate.class),
                resultSet.getObject("effective_to", java.time.LocalDate.class),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static ReferenceRangeSegment mapSegment(ResultSet resultSet, int rowNumber) throws SQLException {
        int ageMin = resultSet.getInt("age_min_days");
        boolean ageMinNull = resultSet.wasNull();
        int ageMax = resultSet.getInt("age_max_days");
        boolean ageMaxNull = resultSet.wasNull();
        return new ReferenceRangeSegment(
                resultSet.getString("segment_id"),
                resultSet.getString("range_id"),
                resultSet.getString("sex"),
                ageMinNull ? null : ageMin,
                ageMaxNull ? null : ageMax,
                resultSet.getString("condition"),
                resultSet.getBigDecimal("normal_low"),
                resultSet.getBigDecimal("normal_high"),
                resultSet.getBigDecimal("critical_low"),
                resultSet.getBigDecimal("critical_high"),
                resultSet.getString("unit"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
