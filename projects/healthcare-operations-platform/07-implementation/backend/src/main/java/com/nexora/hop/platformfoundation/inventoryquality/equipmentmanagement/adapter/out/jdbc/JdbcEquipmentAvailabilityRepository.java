package com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityChange;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcEquipmentAvailabilityRepository implements EquipmentAvailabilityRepository {

  private static final String COLUMNS =
      "change_id, inventory_item_id, tenant_id, branch_id, previous_status, new_status,"
          + " reason_code, changed_by, changed_at, created_by, created_at, updated_by, updated_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcEquipmentAvailabilityRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public EquipmentAvailabilityChange save(EquipmentAvailabilityChange change) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.equipment_availability_changes (
            change_id, inventory_item_id, tenant_id, branch_id, previous_status, new_status,
            reason_code, changed_by, changed_at, created_by, created_at, updated_by, updated_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        change.changeId(),
        change.inventoryItemId(),
        change.tenantId(),
        change.branchId(),
        change.previousStatus(),
        change.newStatus(),
        change.reasonCode(),
        change.changedBy(),
        Timestamp.valueOf(change.changedAt()),
        change.audit().createdBy(),
        Timestamp.valueOf(change.audit().createdAt()),
        change.audit().updatedBy(),
        Timestamp.valueOf(change.audit().updatedAt()));
    return change;
  }

  @Override
  public List<EquipmentAvailabilityChange> findByInventoryItemId(String inventoryItemId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.equipment_availability_changes"
            + " where inventory_item_id = ? order by changed_at",
        JdbcEquipmentAvailabilityRepository::map,
        inventoryItemId);
  }

  private static EquipmentAvailabilityChange map(ResultSet rs, int rowNumber) throws SQLException {
    AuditMetadata audit =
        new AuditMetadata(
            rs.getString("created_by"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getString("updated_by"),
            rs.getTimestamp("updated_at").toLocalDateTime());
    return new EquipmentAvailabilityChange(
        rs.getString("change_id"),
        rs.getString("inventory_item_id"),
        rs.getString("tenant_id"),
        rs.getString("branch_id"),
        rs.getString("previous_status"),
        rs.getString("new_status"),
        rs.getString("reason_code"),
        rs.getString("changed_by"),
        rs.getTimestamp("changed_at").toLocalDateTime(),
        audit);
  }
}
