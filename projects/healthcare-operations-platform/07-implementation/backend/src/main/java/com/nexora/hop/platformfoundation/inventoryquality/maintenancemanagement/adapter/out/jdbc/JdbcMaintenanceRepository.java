package com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain.MaintenanceEvent;
import com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain.MaintenanceRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
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
class JdbcMaintenanceRepository implements MaintenanceRepository {

  private static final String COLUMNS =
      "maintenance_event_id, inventory_item_id, tenant_id, branch_id, maintenance_type,"
          + " performed_by, external_technician_ref, description, started_at, completed_at,"
          + " downtime_minutes, next_scheduled_at, created_by, created_at, updated_by, updated_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcMaintenanceRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public MaintenanceEvent save(MaintenanceEvent event) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.maintenance_events (
            maintenance_event_id, inventory_item_id, tenant_id, branch_id, maintenance_type,
            performed_by, external_technician_ref, description, started_at, completed_at,
            downtime_minutes, next_scheduled_at, created_by, created_at, updated_by, updated_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        on conflict (maintenance_event_id) do update set
            completed_at = excluded.completed_at, downtime_minutes = excluded.downtime_minutes,
            next_scheduled_at = excluded.next_scheduled_at, updated_by = excluded.updated_by,
            updated_at = excluded.updated_at
        """,
        event.maintenanceEventId(),
        event.inventoryItemId(),
        event.tenantId(),
        event.branchId(),
        event.maintenanceType(),
        event.performedBy(),
        event.externalTechnicianRef(),
        event.description(),
        Timestamp.valueOf(event.startedAt()),
        event.completedAt() == null ? null : Timestamp.valueOf(event.completedAt()),
        event.downtimeMinutes(),
        event.nextScheduledAt() == null ? null : Timestamp.valueOf(event.nextScheduledAt()),
        event.audit().createdBy(),
        Timestamp.valueOf(event.audit().createdAt()),
        event.audit().updatedBy(),
        Timestamp.valueOf(event.audit().updatedAt()));
    return event;
  }

  @Override
  public Optional<MaintenanceEvent> findById(String maintenanceEventId) {
    return jdbcTemplate
        .query(
            "select "
                + COLUMNS
                + " from inventory_quality.maintenance_events where maintenance_event_id = ?",
            JdbcMaintenanceRepository::map,
            maintenanceEventId)
        .stream()
        .findFirst();
  }

  @Override
  public List<MaintenanceEvent> findByInventoryItemId(String inventoryItemId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.maintenance_events where inventory_item_id = ?"
            + " order by started_at",
        JdbcMaintenanceRepository::map,
        inventoryItemId);
  }

  private static MaintenanceEvent map(ResultSet rs, int rowNumber) throws SQLException {
    Timestamp completedAt = rs.getTimestamp("completed_at");
    Timestamp nextScheduledAt = rs.getTimestamp("next_scheduled_at");
    AuditMetadata audit =
        new AuditMetadata(
            rs.getString("created_by"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getString("updated_by"),
            rs.getTimestamp("updated_at").toLocalDateTime());
    return new MaintenanceEvent(
        rs.getString("maintenance_event_id"),
        rs.getString("inventory_item_id"),
        rs.getString("tenant_id"),
        rs.getString("branch_id"),
        rs.getString("maintenance_type"),
        rs.getString("performed_by"),
        rs.getString("external_technician_ref"),
        rs.getString("description"),
        rs.getTimestamp("started_at").toLocalDateTime(),
        completedAt == null ? null : completedAt.toLocalDateTime(),
        (Integer) rs.getObject("downtime_minutes"),
        nextScheduledAt == null ? null : nextScheduledAt.toLocalDateTime(),
        audit);
  }
}
