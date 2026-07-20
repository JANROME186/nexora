package com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain.AdjustmentRecord;
import com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain.AdjustmentRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcAdjustmentRepository implements AdjustmentRepository {

  private static final String COLUMNS =
      "adjustment_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id, branch_id,"
          + " delta_quantity, reason_code, reason_note, approver_id, requested_by, occurred_at,"
          + " created_by, created_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcAdjustmentRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public AdjustmentRecord save(AdjustmentRecord record) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.inventory_adjustments (
            adjustment_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id, branch_id,
            delta_quantity, reason_code, reason_note, approver_id, requested_by, occurred_at,
            created_by, created_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        record.adjustmentId(),
        record.inventoryItemId(),
        record.stockLotId(),
        record.tenantId(),
        record.laboratoryId(),
        record.branchId(),
        record.deltaQuantity(),
        record.reasonCode(),
        record.reasonNote(),
        record.approverId(),
        record.requestedBy(),
        Timestamp.valueOf(record.occurredAt()),
        record.createdBy(),
        Timestamp.valueOf(record.createdAt()));
    return record;
  }

  @Override
  public List<AdjustmentRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.inventory_adjustments where tenant_id = ? and laboratory_id = ?"
            + " and branch_id = ? order by occurred_at desc",
        JdbcAdjustmentRepository::map,
        tenantId,
        laboratoryId,
        branchId);
  }

  private static AdjustmentRecord map(ResultSet rs, int rowNumber) throws SQLException {
    return new AdjustmentRecord(
        rs.getString("adjustment_id"),
        rs.getString("inventory_item_id"),
        rs.getString("stock_lot_id"),
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        rs.getBigDecimal("delta_quantity"),
        rs.getString("reason_code"),
        rs.getString("reason_note"),
        rs.getString("approver_id"),
        rs.getString("requested_by"),
        rs.getTimestamp("occurred_at").toLocalDateTime(),
        rs.getString("created_by"),
        rs.getTimestamp("created_at").toLocalDateTime());
  }
}
