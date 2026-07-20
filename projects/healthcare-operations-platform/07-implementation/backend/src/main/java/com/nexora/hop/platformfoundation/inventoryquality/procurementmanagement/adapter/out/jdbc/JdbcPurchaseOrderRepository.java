package com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.SupplierSnapshot;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrder;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrderLine;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrderRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
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
class JdbcPurchaseOrderRepository implements PurchaseOrderRepository {

  private static final String HEADER_COLUMNS =
      "purchase_order_id, tenant_id, laboratory_id, branch_id, supplier_id, supplier_name, status,"
          + " total_amount, currency_code, approver_id, cancellation_reason, created_by, created_at,"
          + " updated_by, updated_at";
  private static final String LINE_COLUMNS =
      "purchase_order_line_id, purchase_order_id, inventory_item_id, ordered_quantity, unit_cost,"
          + " received_quantity, line_status";

  private final JdbcTemplate jdbcTemplate;

  JdbcPurchaseOrderRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public PurchaseOrder save(PurchaseOrder order) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.purchase_orders (
            purchase_order_id, tenant_id, laboratory_id, branch_id, supplier_id, supplier_name,
            status, total_amount, currency_code, approver_id, cancellation_reason, created_by,
            created_at, updated_by, updated_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        on conflict (purchase_order_id) do update set
            status = excluded.status, total_amount = excluded.total_amount,
            approver_id = excluded.approver_id,
            cancellation_reason = excluded.cancellation_reason,
            updated_by = excluded.updated_by, updated_at = excluded.updated_at
        """,
        order.purchaseOrderId(),
        order.tenantId(),
        order.laboratoryId(),
        order.branchId(),
        order.supplier().supplierId(),
        order.supplier().supplierName(),
        order.status(),
        order.totalAmount(),
        order.currencyCode(),
        order.approverId(),
        order.cancellationReason(),
        order.audit().createdBy(),
        Timestamp.valueOf(order.audit().createdAt()),
        order.audit().updatedBy(),
        Timestamp.valueOf(order.audit().updatedAt()));

    // Replace lines wholesale to keep line state in sync.
    jdbcTemplate.update(
        "delete from inventory_quality.purchase_order_lines where purchase_order_id = ?",
        order.purchaseOrderId());
    for (PurchaseOrderLine line : order.lines()) {
      jdbcTemplate.update(
          """
          insert into inventory_quality.purchase_order_lines (
              purchase_order_line_id, purchase_order_id, inventory_item_id, ordered_quantity,
              unit_cost, received_quantity, line_status)
          values (?, ?, ?, ?, ?, ?, ?)
          """,
          line.purchaseOrderLineId(),
          order.purchaseOrderId(),
          line.inventoryItemId(),
          line.orderedQuantity(),
          line.unitCost(),
          line.receivedQuantity(),
          line.lineStatus());
    }
    return order;
  }

  @Override
  public Optional<PurchaseOrder> findById(String purchaseOrderId) {
    return jdbcTemplate
        .query(
            "select "
                + HEADER_COLUMNS
                + " from inventory_quality.purchase_orders where purchase_order_id = ?",
            (rs, rn) -> mapWithLines(rs),
            purchaseOrderId)
        .stream()
        .findFirst();
  }

  @Override
  public List<PurchaseOrder> findByScope(String tenantId, String laboratoryId, String branchId) {
    return jdbcTemplate.query(
        "select "
            + HEADER_COLUMNS
            + " from inventory_quality.purchase_orders where tenant_id = ? and laboratory_id = ?"
            + " and branch_id = ? order by created_at desc",
        (rs, rn) -> mapWithLines(rs),
        tenantId,
        laboratoryId,
        branchId);
  }

  private PurchaseOrder mapWithLines(ResultSet rs) throws SQLException {
    String purchaseOrderId = rs.getString("purchase_order_id");
    List<PurchaseOrderLine> lines =
        jdbcTemplate.query(
            "select "
                + LINE_COLUMNS
                + " from inventory_quality.purchase_order_lines where purchase_order_id = ?",
            JdbcPurchaseOrderRepository::mapLine,
            purchaseOrderId);
    AuditMetadata audit =
        new AuditMetadata(
            rs.getString("created_by"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getString("updated_by"),
            rs.getTimestamp("updated_at").toLocalDateTime());
    return new PurchaseOrder(
        purchaseOrderId,
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        new SupplierSnapshot(rs.getString("supplier_id"), rs.getString("supplier_name")),
        rs.getString("status"),
        List.copyOf(lines),
        Optional.ofNullable(rs.getBigDecimal("total_amount")).orElse(BigDecimal.ZERO),
        rs.getString("currency_code"),
        rs.getString("approver_id"),
        rs.getString("cancellation_reason"),
        audit);
  }

  private static PurchaseOrderLine mapLine(ResultSet rs, int rowNumber) throws SQLException {
    return new PurchaseOrderLine(
        rs.getString("purchase_order_line_id"),
        rs.getString("inventory_item_id"),
        rs.getBigDecimal("ordered_quantity"),
        rs.getBigDecimal("unit_cost"),
        rs.getBigDecimal("received_quantity"),
        rs.getString("line_status"));
  }
}
