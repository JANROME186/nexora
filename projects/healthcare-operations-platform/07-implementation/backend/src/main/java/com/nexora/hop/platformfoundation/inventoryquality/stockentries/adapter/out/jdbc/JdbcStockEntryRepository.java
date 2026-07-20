package com.nexora.hop.platformfoundation.inventoryquality.stockentries.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain.StockEntryRecord;
import com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain.StockEntryRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcStockEntryRepository implements StockEntryRepository {

  private static final String COLUMNS =
      "stock_entry_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id, branch_id,"
          + " purchase_order_line_id, quantity, entry_type, reason_code, received_at,"
          + " created_by, created_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcStockEntryRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public StockEntryRecord save(StockEntryRecord entry) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.stock_entries (
            stock_entry_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id, branch_id,
            purchase_order_line_id, quantity, entry_type, reason_code, received_at, created_by,
            created_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        entry.stockEntryId(),
        entry.inventoryItemId(),
        entry.stockLotId(),
        entry.tenantId(),
        entry.laboratoryId(),
        entry.branchId(),
        entry.purchaseOrderLineId(),
        entry.quantity(),
        entry.entryType(),
        entry.reasonCode(),
        Timestamp.valueOf(entry.receivedAt()),
        entry.createdBy(),
        Timestamp.valueOf(entry.createdAt()));
    return entry;
  }

  @Override
  public List<StockEntryRecord> findByInventoryItemId(String inventoryItemId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.stock_entries where inventory_item_id = ? order by received_at desc",
        JdbcStockEntryRepository::map,
        inventoryItemId);
  }

  @Override
  public List<StockEntryRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.stock_entries where tenant_id = ? and laboratory_id = ?"
            + " and branch_id = ? order by received_at desc",
        JdbcStockEntryRepository::map,
        tenantId,
        laboratoryId,
        branchId);
  }

  private static StockEntryRecord map(ResultSet rs, int rowNumber) throws SQLException {
    return new StockEntryRecord(
        rs.getString("stock_entry_id"),
        rs.getString("inventory_item_id"),
        rs.getString("stock_lot_id"),
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        rs.getString("purchase_order_line_id"),
        rs.getBigDecimal("quantity"),
        rs.getString("entry_type"),
        rs.getString("reason_code"),
        rs.getTimestamp("received_at").toLocalDateTime(),
        rs.getString("created_by"),
        rs.getTimestamp("created_at").toLocalDateTime());
  }
}
