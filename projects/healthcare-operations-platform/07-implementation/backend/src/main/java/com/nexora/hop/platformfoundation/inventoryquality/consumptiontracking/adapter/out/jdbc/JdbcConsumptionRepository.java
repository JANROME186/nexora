package com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain.ConsumptionRecord;
import com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain.ConsumptionRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcConsumptionRepository implements ConsumptionRepository {

  private static final String COLUMNS =
      "consumption_record_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id,"
          + " branch_id, diagnostic_order_id, test_definition_id, consumed_quantity,"
          + " consumption_context, occurred_at, created_by, created_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcConsumptionRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public ConsumptionRecord save(ConsumptionRecord record) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.consumption_records (
            consumption_record_id, inventory_item_id, stock_lot_id, tenant_id, laboratory_id,
            branch_id, diagnostic_order_id, test_definition_id, consumed_quantity,
            consumption_context, occurred_at, created_by, created_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        record.consumptionRecordId(),
        record.inventoryItemId(),
        record.stockLotId(),
        record.tenantId(),
        record.laboratoryId(),
        record.branchId(),
        record.diagnosticOrderId(),
        record.testDefinitionId(),
        record.consumedQuantity(),
        record.consumptionContext(),
        Timestamp.valueOf(record.occurredAt()),
        record.createdBy(),
        Timestamp.valueOf(record.createdAt()));
    return record;
  }

  @Override
  public List<ConsumptionRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.consumption_records where tenant_id = ? and laboratory_id = ?"
            + " and branch_id = ? order by occurred_at desc",
        JdbcConsumptionRepository::map,
        tenantId,
        laboratoryId,
        branchId);
  }

  private static ConsumptionRecord map(ResultSet rs, int rowNumber) throws SQLException {
    return new ConsumptionRecord(
        rs.getString("consumption_record_id"),
        rs.getString("inventory_item_id"),
        rs.getString("stock_lot_id"),
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        rs.getString("diagnostic_order_id"),
        rs.getString("test_definition_id"),
        rs.getBigDecimal("consumed_quantity"),
        rs.getString("consumption_context"),
        rs.getTimestamp("occurred_at").toLocalDateTime(),
        rs.getString("created_by"),
        rs.getTimestamp("created_at").toLocalDateTime());
  }
}
