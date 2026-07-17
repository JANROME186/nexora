package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.adapter.out.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.AnalyteSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.CaptureSource;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.CriticalResultFlag;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.IncidentType;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.MedicalValidationRecord;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ProcessingIncident;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ReferenceRangeSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultReleaseRecord;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultStatus;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultValue;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.TechnicalValidationRecord;

/**
 * JDBC repository for LaboratoryResult aggregate (local profile with PostgreSQL).
 */
@Repository
@Profile("local")
class JdbcLaboratoryResultsRepository implements LaboratoryResultsRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcLaboratoryResultsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public LaboratoryResult save(LaboratoryResult result) {
        jdbcTemplate.update("""
                insert into laboratory_results.results (
                    result_id, tenant_id, laboratory_id, branch_id, order_id, sample_id,
                    test_definition_id, analyte_id, analyte_version, analyte_name, analyte_unit,
                    analyte_method, analyte_snapshot_at,
                    range_id, range_version, range_low, range_high, range_critical_low,
                    range_critical_high, range_snapshot_at,
                    raw_value, numeric_value, unit, method, captured_at, captured_by, device_reference,
                    capture_source,
                    tech_validated_by, tech_validated_at,
                    critical_flagged_by, critical_flagged_at, critical_reason,
                    med_validated_by, med_validated_at,
                    released_by, released_at,
                    status, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (result_id) do update set
                    tech_validated_by = excluded.tech_validated_by,
                    tech_validated_at = excluded.tech_validated_at,
                    critical_flagged_by = excluded.critical_flagged_by,
                    critical_flagged_at = excluded.critical_flagged_at,
                    critical_reason = excluded.critical_reason,
                    med_validated_by = excluded.med_validated_by,
                    med_validated_at = excluded.med_validated_at,
                    released_by = excluded.released_by,
                    released_at = excluded.released_at,
                    status = excluded.status,
                    updated_at = excluded.updated_at
                """,
                result.resultId(), result.tenantId(), result.laboratoryId(), result.branchId(),
                result.orderId(), result.sampleId(),
                result.analyteSnapshot().testDefinitionId(), result.analyteSnapshot().analyteId(),
                result.analyteSnapshot().publishedVersion(), result.analyteSnapshot().name(),
                result.analyteSnapshot().unit(), result.analyteSnapshot().method(),
                ts(result.analyteSnapshot().capturedAt()),
                result.referenceRangeSnapshot().referenceRangeId(),
                result.referenceRangeSnapshot().publishedVersion(),
                result.referenceRangeSnapshot().lowValue(), result.referenceRangeSnapshot().highValue(),
                result.referenceRangeSnapshot().criticalLowValue(),
                result.referenceRangeSnapshot().criticalHighValue(),
                ts(result.referenceRangeSnapshot().capturedAt()),
                result.resultValue().rawValue(), result.resultValue().numericValue(),
                result.resultValue().unit(), result.resultValue().method(),
                ts(result.resultValue().capturedAt()), result.resultValue().capturedBy(),
                result.resultValue().deviceReference(),
                result.captureSource().name(),
                result.technicalValidation() != null ? result.technicalValidation().validatedBy() : null,
                result.technicalValidation() != null ? ts(result.technicalValidation().validatedAt()) : null,
                result.criticalFlag() != null ? result.criticalFlag().flaggedBy() : null,
                result.criticalFlag() != null ? ts(result.criticalFlag().flaggedAt()) : null,
                result.criticalFlag() != null ? result.criticalFlag().criticalReason() : null,
                result.medicalValidation() != null ? result.medicalValidation().validatedBy() : null,
                result.medicalValidation() != null ? ts(result.medicalValidation().validatedAt()) : null,
                result.releaseRecord() != null ? result.releaseRecord().releasedBy() : null,
                result.releaseRecord() != null ? ts(result.releaseRecord().releasedAt()) : null,
                result.status().name(),
                ts(result.createdAt()), ts(result.updatedAt()));

        saveIncidents(result);
        return result;
    }

    private void saveIncidents(LaboratoryResult result) {
        jdbcTemplate.update(
                "delete from laboratory_results.processing_incidents where result_id = ?",
                result.resultId());
        for (ProcessingIncident inc : result.processingIncidents()) {
            jdbcTemplate.update("""
                    insert into laboratory_results.processing_incidents
                        (result_id, incident_type, notes, recorded_by, recorded_at)
                    values (?, ?, ?, ?, ?)
                    """,
                    result.resultId(), inc.incidentType().name(),
                    inc.notes(), inc.recordedBy(), ts(inc.recordedAt()));
        }
    }

    @Override
    public Optional<LaboratoryResult> findById(String resultId, String tenantId) {
        List<LaboratoryResult> results = jdbcTemplate.query(
                "select * from laboratory_results.results where result_id = ? and tenant_id = ?",
                (rs, rn) -> mapResult(rs, loadIncidents(resultId)),
                resultId, tenantId);
        return results.stream().findFirst();
    }

    @Override
    public List<LaboratoryResult> findBySampleId(String sampleId, String tenantId) {
        return jdbcTemplate.query(
                "select * from laboratory_results.results where sample_id = ? and tenant_id = ?",
                (rs, rn) -> mapResult(rs, loadIncidents(rs.getString("result_id"))),
                sampleId, tenantId);
    }

    @Override
    public List<LaboratoryResult> findByStatus(ResultStatus status, String tenantId) {
        return jdbcTemplate.query(
                "select * from laboratory_results.results where status = ? and tenant_id = ?",
                (rs, rn) -> mapResult(rs, loadIncidents(rs.getString("result_id"))),
                status.name(), tenantId);
    }

    @Override
    public List<LaboratoryResult> findProcessingWorklist(String tenantId, String laboratoryId) {
        return jdbcTemplate.query("""
                select * from laboratory_results.results
                 where tenant_id = ? and laboratory_id = ? and status = ?
                """,
                (rs, rn) -> mapResult(rs, loadIncidents(rs.getString("result_id"))),
                tenantId, laboratoryId, ResultStatus.captured.name());
    }

    @Override
    public List<LaboratoryResult> findTechnicalValidationWorklist(String tenantId,
            String laboratoryId) {
        return jdbcTemplate.query("""
                select * from laboratory_results.results
                 where tenant_id = ? and laboratory_id = ? and status = ?
                """,
                (rs, rn) -> mapResult(rs, loadIncidents(rs.getString("result_id"))),
                tenantId, laboratoryId, ResultStatus.pending_technical_validation.name());
    }

    @Override
    public List<LaboratoryResult> findMedicalValidationWorklist(String tenantId,
            String laboratoryId) {
        return jdbcTemplate.query("""
                select * from laboratory_results.results
                 where tenant_id = ? and laboratory_id = ? and status = ?
                """,
                (rs, rn) -> mapResult(rs, loadIncidents(rs.getString("result_id"))),
                tenantId, laboratoryId, ResultStatus.pending_medical_validation.name());
    }

    @Override
    public List<LaboratoryResult> findReleaseWorklist(String tenantId, String laboratoryId) {
        return jdbcTemplate.query("""
                select * from laboratory_results.results
                 where tenant_id = ? and laboratory_id = ? and status = ?
                """,
                (rs, rn) -> mapResult(rs, loadIncidents(rs.getString("result_id"))),
                tenantId, laboratoryId, ResultStatus.medically_validated.name());
    }

    private List<ProcessingIncident> loadIncidents(String resultId) {
        return jdbcTemplate.query(
                "select * from laboratory_results.processing_incidents where result_id = ?",
                (rs, rn) -> new ProcessingIncident(
                        IncidentType.valueOf(rs.getString("incident_type")),
                        rs.getString("notes"), rs.getString("recorded_by"),
                        instant(rs.getTimestamp("recorded_at"))),
                resultId);
    }

    private static LaboratoryResult mapResult(ResultSet rs,
            List<ProcessingIncident> incidents) throws SQLException {
        AnalyteSnapshot analyteSnapshot = new AnalyteSnapshot(
                rs.getString("test_definition_id"), rs.getString("analyte_id"),
                rs.getInt("analyte_version"), rs.getString("analyte_name"),
                rs.getString("analyte_unit"), rs.getString("analyte_method"),
                instant(rs.getTimestamp("analyte_snapshot_at")));

        ReferenceRangeSnapshot referenceRangeSnapshot = new ReferenceRangeSnapshot(
                rs.getString("range_id"), rs.getInt("range_version"),
                rs.getString("range_low"), rs.getString("range_high"),
                rs.getString("range_critical_low"), rs.getString("range_critical_high"),
                instant(rs.getTimestamp("range_snapshot_at")));

        String numericStr = rs.getString("numeric_value");
        BigDecimal numericValue = numericStr != null ? new BigDecimal(numericStr) : null;
        ResultValue resultValue = new ResultValue(
                rs.getString("raw_value"), numericValue, rs.getString("unit"),
                rs.getString("method"), instant(rs.getTimestamp("captured_at")),
                rs.getString("captured_by"), rs.getString("device_reference"));

        String techBy = rs.getString("tech_validated_by");
        TechnicalValidationRecord techValidation = techBy != null
                ? new TechnicalValidationRecord(techBy, instant(rs.getTimestamp("tech_validated_at")))
                : null;

        String critBy = rs.getString("critical_flagged_by");
        CriticalResultFlag criticalFlag = critBy != null
                ? new CriticalResultFlag(critBy, instant(rs.getTimestamp("critical_flagged_at")),
                        rs.getString("critical_reason"))
                : null;

        String medBy = rs.getString("med_validated_by");
        MedicalValidationRecord medicalValidation = medBy != null
                ? new MedicalValidationRecord(medBy, instant(rs.getTimestamp("med_validated_at")))
                : null;

        String relBy = rs.getString("released_by");
        ResultReleaseRecord releaseRecord = relBy != null
                ? new ResultReleaseRecord(relBy, instant(rs.getTimestamp("released_at")))
                : null;

        return new LaboratoryResult(
                rs.getString("result_id"), rs.getString("tenant_id"),
                rs.getString("laboratory_id"), rs.getString("branch_id"),
                rs.getString("order_id"), rs.getString("sample_id"),
                analyteSnapshot, referenceRangeSnapshot, resultValue,
                CaptureSource.valueOf(rs.getString("capture_source")),
                new ArrayList<>(incidents), techValidation, criticalFlag,
                medicalValidation, releaseRecord, List.of(),
                ResultStatus.valueOf(rs.getString("status")),
                instant(rs.getTimestamp("created_at")), instant(rs.getTimestamp("updated_at")));
    }

    private static Timestamp ts(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    private static Instant instant(Timestamp ts) {
        return ts != null ? ts.toInstant() : null;
    }
}
