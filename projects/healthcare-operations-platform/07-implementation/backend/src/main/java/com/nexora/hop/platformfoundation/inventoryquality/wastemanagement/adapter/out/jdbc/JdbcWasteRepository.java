package com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain.WasteRecord;
import com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain.WasteRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcWasteRepository implements WasteRepository {

  private static final String COLUMNS =
      "waste_record_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id, branch_id,"
          + " disposed_quantity, reason_code, reason_note, disposed_at, created_by, created_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcWasteRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public WasteRecord save(WasteRecord record) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.waste_records (
            waste_record_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id, branch_id,
            disposed_quantity, reason_code, reason_note, disposed_at, created_by, created_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        record.wasteRecordId(),
        record.inventoryItemId(),
        record.stockLotId(),
        record.tenantId(),
        record.laboratoryId(),
        record.branchId(),
        record.disposedQuantity(),
        record.reasonCode(),
        record.reasonNote(),
        Timestamp.valueOf(record.disposedAt()),
        record.createdBy(),
        Timestamp.valueOf(record.createdAt()));
    return record;
  }

  @Override
  public List<WasteRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.waste_records where tenant_id = ? and laboratory_id = ?"
            + " and branch_id = ? order by disposed_at desc",
        JdbcWasteRepository::map,
        tenantId,
        laboratoryId,
        branchId);
  }

  private static WasteRecord map(ResultSet rs, int rowNumber) throws SQLException {
    return new WasteRecord(
        rs.getString("waste_record_id"),
        rs.getString("inventory_item_id"),
        rs.getString("stock_lot_id"),
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        rs.getBigDecimal("disposed_quantity"),
        rs.getString("reason_code"),
        rs.getString("reason_note"),
        rs.getTimestamp("disposed_at").toLocalDateTime(),
        rs.getString("created_by"),
        rs.getTimestamp("created_at").toLocalDateTime());
  }
}
