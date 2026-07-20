package com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.ExpectedRange;
import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.QualityControlRun;
import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.QualityControlRunRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcQualityControlRunRepository implements QualityControlRunRepository {

  private static final String COLUMNS =
      "qc_run_id, tenant_id, laboratory_id, branch_id, test_definition_id,"
          + " control_material_stock_lot_id, measured_value, expected_min, expected_max,"
          + " expected_captured_at, rule_evaluation, acceptance_decision,"
          + " linked_laboratory_result_ids, performed_by, performed_at, evidence_reference,"
          + " override_reason, override_by, created_by, created_at, updated_by, updated_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcQualityControlRunRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public QualityControlRun save(QualityControlRun run) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.quality_control_runs (
            qc_run_id, tenant_id, laboratory_id, branch_id, test_definition_id,
            control_material_stock_lot_id, measured_value, expected_min, expected_max,
            expected_captured_at, rule_evaluation, acceptance_decision,
            linked_laboratory_result_ids, performed_by, performed_at, evidence_reference,
            override_reason, override_by, created_by, created_at, updated_by, updated_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        on conflict (qc_run_id) do update set
            acceptance_decision = excluded.acceptance_decision,
            override_reason = excluded.override_reason, override_by = excluded.override_by,
            updated_by = excluded.updated_by, updated_at = excluded.updated_at
        """,
        run.qcRunId(),
        run.tenantId(),
        run.laboratoryId(),
        run.branchId(),
        run.testDefinitionId(),
        run.controlMaterialStockLotId(),
        run.measuredValue(),
        run.expectedRange().min(),
        run.expectedRange().max(),
        Timestamp.valueOf(run.expectedRange().capturedAt()),
        run.ruleEvaluation(),
        run.acceptanceDecision(),
        String.join(",", run.linkedLaboratoryResultIds()),
        run.performedBy(),
        Timestamp.valueOf(run.performedAt()),
        run.evidenceReference(),
        run.overrideReason(),
        run.overrideBy(),
        run.audit().createdBy(),
        Timestamp.valueOf(run.audit().createdAt()),
        run.audit().updatedBy(),
        Timestamp.valueOf(run.audit().updatedAt()));
    return run;
  }

  @Override
  public Optional<QualityControlRun> findById(String qcRunId) {
    return jdbcTemplate
        .query(
            "select " + COLUMNS + " from inventory_quality.quality_control_runs where qc_run_id = ?",
            JdbcQualityControlRunRepository::map,
            qcRunId)
        .stream()
        .findFirst();
  }

  @Override
  public List<QualityControlRun> findByScope(
      String tenantId, String laboratoryId, String branchId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.quality_control_runs where tenant_id = ?"
            + " and laboratory_id = ? and branch_id = ? order by performed_at",
        JdbcQualityControlRunRepository::map,
        tenantId,
        laboratoryId,
        branchId);
  }

  private static QualityControlRun map(ResultSet rs, int rowNumber) throws SQLException {
    AuditMetadata audit =
        new AuditMetadata(
            rs.getString("created_by"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getString("updated_by"),
            rs.getTimestamp("updated_at").toLocalDateTime());
    String linked = rs.getString("linked_laboratory_result_ids");
    List<String> linkedIds =
        linked == null || linked.isBlank() ? List.of() : Arrays.asList(linked.split(","));
    return new QualityControlRun(
        rs.getString("qc_run_id"),
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        rs.getString("test_definition_id"),
        rs.getString("control_material_stock_lot_id"),
        rs.getBigDecimal("measured_value"),
        new ExpectedRange(
            rs.getBigDecimal("expected_min"),
            rs.getBigDecimal("expected_max"),
            rs.getTimestamp("expected_captured_at").toLocalDateTime()),
        rs.getString("rule_evaluation"),
        rs.getString("acceptance_decision"),
        linkedIds,
        rs.getString("performed_by"),
        rs.getTimestamp("performed_at").toLocalDateTime(),
        rs.getString("evidence_reference"),
        rs.getString("override_reason"),
        rs.getString("override_by"),
        audit);
  }
}
