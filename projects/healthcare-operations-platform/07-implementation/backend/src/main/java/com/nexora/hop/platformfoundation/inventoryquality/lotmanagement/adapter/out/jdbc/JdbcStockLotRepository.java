package com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLotRepository;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.SupplierSnapshot;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcStockLotRepository implements StockLotRepository {

  private static final String COLUMNS =
      "stock_lot_id, inventory_item_id, tenant_id, laboratory_id, branch_id, lot_number,"
          + " supplier_id, supplier_name, expiration_date, received_quantity, remaining_quantity,"
          + " status, created_by, created_at, updated_by, updated_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcStockLotRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public StockLot save(StockLot lot) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.stock_lots (
            stock_lot_id, inventory_item_id, tenant_id, laboratory_id, branch_id, lot_number,
            supplier_id, supplier_name, expiration_date, received_quantity, remaining_quantity,
            status, created_by, created_at, updated_by, updated_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        on conflict (stock_lot_id) do update set
            supplier_id = excluded.supplier_id, supplier_name = excluded.supplier_name,
            expiration_date = excluded.expiration_date,
            remaining_quantity = excluded.remaining_quantity, status = excluded.status,
            updated_by = excluded.updated_by, updated_at = excluded.updated_at
        """,
        lot.stockLotId(),
        lot.inventoryItemId(),
        lot.tenantId(),
        lot.laboratoryId(),
        lot.branchId(),
        lot.lotNumber(),
        lot.supplier() == null ? null : lot.supplier().supplierId(),
        lot.supplier() == null ? null : lot.supplier().supplierName(),
        lot.expirationDate() == null ? null : Date.valueOf(lot.expirationDate()),
        lot.receivedQuantity(),
        lot.remainingQuantity(),
        lot.status(),
        lot.audit().createdBy(),
        Timestamp.valueOf(lot.audit().createdAt()),
        lot.audit().updatedBy(),
        Timestamp.valueOf(lot.audit().updatedAt()));
    return lot;
  }

  @Override
  public Optional<StockLot> findById(String stockLotId) {
    return jdbcTemplate
        .query(
            "select " + COLUMNS + " from inventory_quality.stock_lots where stock_lot_id = ?",
            JdbcStockLotRepository::map,
            stockLotId)
        .stream()
        .findFirst();
  }

  @Override
  public Optional<StockLot> findByInventoryItemIdAndLotNumber(
      String inventoryItemId, String lotNumber) {
    return jdbcTemplate
        .query(
            "select "
                + COLUMNS
                + " from inventory_quality.stock_lots where inventory_item_id = ? and lot_number = ?",
            JdbcStockLotRepository::map,
            inventoryItemId,
            lotNumber)
        .stream()
        .findFirst();
  }

  @Override
  public List<StockLot> findByInventoryItemId(String inventoryItemId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.stock_lots where inventory_item_id = ? order by lot_number",
        JdbcStockLotRepository::map,
        inventoryItemId);
  }

  private static StockLot map(ResultSet rs, int rowNumber) throws SQLException {
    String supplierId = rs.getString("supplier_id");
    String supplierName = rs.getString("supplier_name");
    SupplierSnapshot supplier =
        supplierId == null && supplierName == null
            ? null
            : new SupplierSnapshot(supplierId, supplierName);
    Date expiration = rs.getDate("expiration_date");
    AuditMetadata audit =
        new AuditMetadata(
            rs.getString("created_by"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getString("updated_by"),
            rs.getTimestamp("updated_at").toLocalDateTime());
    return new StockLot(
        rs.getString("stock_lot_id"),
        rs.getString("inventory_item_id"),
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        rs.getString("lot_number"),
        supplier,
        expiration == null ? null : expiration.toLocalDate(),
        rs.getBigDecimal("received_quantity"),
        rs.getBigDecimal("remaining_quantity"),
        rs.getString("status"),
        audit);
  }
}
