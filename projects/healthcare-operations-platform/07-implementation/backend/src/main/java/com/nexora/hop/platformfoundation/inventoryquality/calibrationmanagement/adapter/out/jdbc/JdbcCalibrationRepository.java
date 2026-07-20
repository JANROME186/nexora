package com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain.CalibrationEvent;
import com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain.CalibrationRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
class JdbcCalibrationRepository implements CalibrationRepository {

  private static final String COLUMNS =
      "calibration_event_id, inventory_item_id, tenant_id, branch_id, calibration_standard_ref,"
          + " performed_by, performed_at, result, next_due_date, certificate_reference,"
          + " created_by, created_at, updated_by, updated_at";

  private final JdbcTemplate jdbcTemplate;

  JdbcCalibrationRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public CalibrationEvent save(CalibrationEvent event) {
    jdbcTemplate.update(
        """
        insert into inventory_quality.calibration_events (
            calibration_event_id, inventory_item_id, tenant_id, branch_id,
            calibration_standard_ref, performed_by, performed_at, result, next_due_date,
            certificate_reference, created_by, created_at, updated_by, updated_at)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        event.calibrationEventId(),
        event.inventoryItemId(),
        event.tenantId(),
        event.branchId(),
        event.calibrationStandardRef(),
        event.performedBy(),
        Timestamp.valueOf(event.performedAt()),
        event.result(),
        event.nextDueDate() == null ? null : Date.valueOf(event.nextDueDate()),
        event.certificateReference(),
        event.audit().createdBy(),
        Timestamp.valueOf(event.audit().createdAt()),
        event.audit().updatedBy(),
        Timestamp.valueOf(event.audit().updatedAt()));
    return event;
  }

  @Override
  public List<CalibrationEvent> findByInventoryItemId(String inventoryItemId) {
    return jdbcTemplate.query(
        "select "
            + COLUMNS
            + " from inventory_quality.calibration_events where inventory_item_id = ?"
            + " order by performed_at",
        JdbcCalibrationRepository::map,
        inventoryItemId);
  }

  private static CalibrationEvent map(ResultSet rs, int rowNumber) throws SQLException {
    Date nextDueDate = rs.getDate("next_due_date");
    AuditMetadata audit =
        new AuditMetadata(
            rs.getString("created_by"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getString("updated_by"),
            rs.getTimestamp("updated_at").toLocalDateTime());
    return new CalibrationEvent(
        rs.getString("calibration_event_id"),
        rs.getString("inventory_item_id"),
        rs.getString("tenant_id"),
        rs.getString("branch_id"),
        rs.getString("calibration_standard_ref"),
        rs.getString("performed_by"),
        rs.getTimestamp("performed_at").toLocalDateTime(),
        rs.getString("result"),
        nextDueDate == null ? null : nextDueDate.toLocalDate(),
        rs.getString("certificate_reference"),
        audit);
  }
}
