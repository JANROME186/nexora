package com.nexora.hop.platformfoundation.inventoryquality.stockexits.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain.StockExitRecord;
import com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain.StockExitRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcStockExitRepository implements StockExitRepository {

  private static final String COLUMNS =
      "stock_exit_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id, branch_id,"
          + " destination_branch_id, quantity, exit_type, reason_code, occurred_at, created_by,"
          + " created_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcStockExitRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public StockExitRecord save(StockExitRecord exit) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.stock_exits (
            stock_exit_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id, branch_id,
            destination_branch_id, quantity, exit_type, reason_code, occurred_at, created_by,
            created_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        exit.stockExitId(),
        exit.inventoryItemId(),
        exit.stockLotId(),
        exit.tenantId(),
        exit.laboratoryId(),
        exit.branchId(),
        exit.destinationBranchId(),
        exit.quantity(),
        exit.exitType(),
        exit.reasonCode(),
        Timestamp.valueOf(exit.occurredAt()),
        exit.createdBy(),
        Timestamp.valueOf(exit.createdAt()));
    return exit;
  }

  @Override
  public List<StockExitRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.stock_exits where tenant_id = ? and laboratory_id = ?"
            + " and branch_id = ? order by occurred_at desc",
        JdbcStockExitRepository::map,
        tenantId,
        laboratoryId,
        branchId);
  }

  private static StockExitRecord map(ResultSet rs, int rowNumber) throws SQLException {
    return new StockExitRecord(
        rs.getString("stock_exit_id"),
        rs.getString("inventory_item_id"),
        rs.getString("stock_lot_id"),
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        rs.getString("destination_branch_id"),
        rs.getBigDecimal("quantity"),
        rs.getString("exit_type"),
        rs.getString("reason_code"),
        rs.getTimestamp("occurred_at").toLocalDateTime(),
        rs.getString("created_by"),
        rs.getTimestamp("created_at").toLocalDateTime());
  }
}
