package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.EquipmentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItemRepository;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.ReagentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.StockSummary;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcInventoryItemRepository implements InventoryItemRepository {

  private static final String COLUMNS =
      "inventory_item_id, tenant_id, laboratory_id, branch_id, item_code, item_name, item_type,"
          + " classification, unit_of_measure, status, on_hand_quantity, reserved_quantity,"
          + " reorder_point, reorder_quantity, last_movement_at, reagent_linked_test_definition_id,"
          + " reagent_category, reagent_consumption_ratio, equipment_asset_tag,"
          + " equipment_serial_number, equipment_manufacturer, equipment_model,"
          + " equipment_installed_at, equipment_location, equipment_availability_status,"
          + " created_by, created_at, updated_by, updated_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcInventoryItemRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public InventoryItem save(InventoryItem item) {
    ReagentProfile reagent = item.reagentProfile();
    EquipmentProfile equipment = item.equipmentProfile();
    StockSummary summary = item.stockSummary() == null ? StockSummary.empty() : item.stockSummary();
    jdbcTemplate.update(
        """
        insert into inventory_quality.inventory_items (
            inventory_item_id, tenant_id, laboratory_id, branch_id, item_code, item_name,
            item_type, classification, unit_of_measure, status, on_hand_quantity,
            reserved_quantity, reorder_point, reorder_quantity, last_movement_at,
            reagent_linked_test_definition_id, reagent_category, reagent_consumption_ratio,
            equipment_asset_tag, equipment_serial_number, equipment_manufacturer, equipment_model,
            equipment_installed_at, equipment_location, equipment_availability_status,
            created_by, created_at, updated_by, updated_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        on conflict (inventory_item_id) do update set
            item_name = excluded.item_name, item_type = excluded.item_type,
            classification = excluded.classification, unit_of_measure = excluded.unit_of_measure,
            status = excluded.status, on_hand_quantity = excluded.on_hand_quantity,
            reserved_quantity = excluded.reserved_quantity, reorder_point = excluded.reorder_point,
            reorder_quantity = excluded.reorder_quantity, last_movement_at = excluded.last_movement_at,
            reagent_linked_test_definition_id = excluded.reagent_linked_test_definition_id,
            reagent_category = excluded.reagent_category,
            reagent_consumption_ratio = excluded.reagent_consumption_ratio,
            equipment_asset_tag = excluded.equipment_asset_tag,
            equipment_serial_number = excluded.equipment_serial_number,
            equipment_manufacturer = excluded.equipment_manufacturer,
            equipment_model = excluded.equipment_model,
            equipment_installed_at = excluded.equipment_installed_at,
            equipment_location = excluded.equipment_location,
            equipment_availability_status = excluded.equipment_availability_status,
            updated_by = excluded.updated_by, updated_at = excluded.updated_at
        """,
        item.inventoryItemId(),
        item.tenantId(),
        item.laboratoryId(),
        item.branchId(),
        item.itemCode(),
        item.itemName(),
        item.itemType(),
        item.classification(),
        item.unitOfMeasure(),
        item.status(),
        summary.onHandQuantity(),
        summary.reservedQuantity(),
        summary.reorderPoint(),
        summary.reorderQuantity(),
        summary.lastMovementAt() == null ? null : Timestamp.valueOf(summary.lastMovementAt()),
        reagent == null ? null : reagent.linkedTestDefinitionId(),
        reagent == null ? null : reagent.reagentCategory(),
        reagent == null ? null : reagent.consumptionUnitRatio(),
        equipment == null ? null : equipment.assetTag(),
        equipment == null ? null : equipment.serialNumber(),
        equipment == null ? null : equipment.manufacturer(),
        equipment == null ? null : equipment.model(),
        equipment == null || equipment.installedAt() == null
            ? null
            : Timestamp.valueOf(equipment.installedAt()),
        equipment == null ? null : equipment.location(),
        equipment == null ? null : equipment.availabilityStatus(),
        item.audit().createdBy(),
        Timestamp.valueOf(item.audit().createdAt()),
        item.audit().updatedBy(),
        Timestamp.valueOf(item.audit().updatedAt()));
    return item;
  }

  @Override
  public Optional<InventoryItem> findById(String inventoryItemId) {
    return jdbcTemplate
        .query(
            "select " + COLUMNS + " from inventory_quality.inventory_items where inventory_item_id = ?",
            JdbcInventoryItemRepository::map,
            inventoryItemId)
        .stream()
        .findFirst();
  }

  @Override
  public Optional<InventoryItem> findByScopeAndCode(
      String tenantId, String laboratoryId, String branchId, String itemCode) {
    return jdbcTemplate
        .query(
            "select "
                + COLUMNS
                + " from inventory_quality.inventory_items where tenant_id = ?"
                + " and laboratory_id = ? and branch_id = ? and item_code = ?",
            JdbcInventoryItemRepository::map,
            tenantId,
            laboratoryId,
            branchId,
            itemCode)
        .stream()
        .findFirst();
  }

  @Override
  public List<InventoryItem> findByScope(String tenantId, String laboratoryId, String branchId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.inventory_items where tenant_id = ?"
            + " and laboratory_id = ? and branch_id = ? order by item_code",
        JdbcInventoryItemRepository::map,
        tenantId,
        laboratoryId,
        branchId);
  }

  private static InventoryItem map(ResultSet rs, int rowNumber) throws SQLException {
    StockSummary summary =
        new StockSummary(
            optionalBigDecimal(rs, "on_hand_quantity").orElse(BigDecimal.ZERO),
            optionalBigDecimal(rs, "reserved_quantity").orElse(BigDecimal.ZERO),
            optionalBigDecimal(rs, "reorder_point").orElse(null),
            optionalBigDecimal(rs, "reorder_quantity").orElse(null),
            optionalTimestamp(rs, "last_movement_at").orElse(null));
    String reagentCategory = rs.getString("reagent_category");
    ReagentProfile reagent =
        reagentCategory == null
            ? null
            : new ReagentProfile(
                rs.getString("reagent_linked_test_definition_id"),
                reagentCategory,
                optionalBigDecimal(rs, "reagent_consumption_ratio").orElse(null));
    String assetTag = rs.getString("equipment_asset_tag");
    EquipmentProfile equipment =
        assetTag == null
            ? null
            : new EquipmentProfile(
                assetTag,
                rs.getString("equipment_serial_number"),
                rs.getString("equipment_manufacturer"),
                rs.getString("equipment_model"),
                optionalTimestamp(rs, "equipment_installed_at").orElse(null),
                rs.getString("equipment_location"),
                rs.getString("equipment_availability_status"));
    AuditMetadata audit =
        new AuditMetadata(
            rs.getString("created_by"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getString("updated_by"),
            rs.getTimestamp("updated_at").toLocalDateTime());
    return new InventoryItem(
        rs.getString("inventory_item_id"),
        rs.getString("tenant_id"),
        rs.getString("laboratory_id"),
        rs.getString("branch_id"),
        rs.getString("item_code"),
        rs.getString("item_name"),
        rs.getString("item_type"),
        rs.getString("classification"),
        rs.getString("unit_of_measure"),
        rs.getString("status"),
        summary,
        reagent,
        equipment,
        audit);
  }

  private static Optional<BigDecimal> optionalBigDecimal(ResultSet rs, String column)
      throws SQLException {
    BigDecimal value = rs.getBigDecimal(column);
    return rs.wasNull() ? Optional.empty() : Optional.ofNullable(value);
  }

  private static Optional<LocalDateTime> optionalTimestamp(ResultSet rs, String column)
      throws SQLException {
    Timestamp value = rs.getTimestamp(column);
    return value == null ? Optional.empty() : Optional.of(value.toLocalDateTime());
  }
}
