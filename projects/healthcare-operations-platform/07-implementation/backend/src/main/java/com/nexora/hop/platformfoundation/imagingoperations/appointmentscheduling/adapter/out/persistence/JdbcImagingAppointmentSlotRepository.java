package com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain.ImagingAppointmentSlot;
import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain.ImagingAppointmentSlotRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
public class JdbcImagingAppointmentSlotRepository implements ImagingAppointmentSlotRepository {

    private static final String SELECT_SQL = """
            select slot_id, tenant_id, patient_id, branch_id, modality, procedure_code,
                   procedure_room_id, start_time, end_time, duration_minutes, slot_status, notes,
                   created_by, created_at, updated_by, updated_at
            from imaging_operations.imaging_appointment_slots
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcImagingAppointmentSlotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ImagingAppointmentSlot save(ImagingAppointmentSlot slot) {
        jdbcTemplate.update("""
                insert into imaging_operations.imaging_appointment_slots
                    (slot_id, tenant_id, patient_id, branch_id, modality, procedure_code,
                     procedure_room_id, start_time, end_time, duration_minutes, slot_status, notes,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (slot_id) do update set
                    modality = excluded.modality, procedure_code = excluded.procedure_code,
                    procedure_room_id = excluded.procedure_room_id, start_time = excluded.start_time,
                    end_time = excluded.end_time, duration_minutes = excluded.duration_minutes,
                    slot_status = excluded.slot_status, notes = excluded.notes,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                slot.slotId(), slot.tenantId(), slot.patientId(), slot.branchId(), slot.modality(),
                slot.procedureCode(), slot.procedureRoomId(), Timestamp.from(slot.startTime()),
                Timestamp.from(slot.endTime()), slot.durationMinutes(), slot.slotStatus(), slot.notes(),
                slot.createdBy(), Timestamp.from(slot.createdAt()), slot.updatedBy(), Timestamp.from(slot.updatedAt()));
        return slot;
    }

    @Override
    public Optional<ImagingAppointmentSlot> findById(String tenantId, String slotId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and slot_id = ?",
                JdbcImagingAppointmentSlotRepository::map, tenantId, slotId).stream().findFirst();
    }

    @Override
    public List<ImagingAppointmentSlot> findByTenantAndPatient(String tenantId, String patientId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and patient_id = ?",
                JdbcImagingAppointmentSlotRepository::map, tenantId, patientId);
    }

    @Override
    public List<ImagingAppointmentSlot> findOverlappingRoomSlots(String tenantId, String procedureRoomId, Instant startTime, Instant endTime) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and procedure_room_id = ? and lower(slot_status) != 'cancelled' and start_time < ? and end_time > ?",
                JdbcImagingAppointmentSlotRepository::map, tenantId, procedureRoomId, Timestamp.from(endTime), Timestamp.from(startTime));
    }

    private static ImagingAppointmentSlot map(ResultSet rs, int rowNum) throws SQLException {
        return new ImagingAppointmentSlot(
                rs.getString("slot_id"),
                rs.getString("tenant_id"),
                rs.getString("patient_id"),
                rs.getString("branch_id"),
                rs.getString("modality"),
                rs.getString("procedure_code"),
                rs.getString("procedure_room_id"),
                rs.getTimestamp("start_time").toInstant(),
                rs.getTimestamp("end_time").toInstant(),
                rs.getInt("duration_minutes"),
                rs.getString("slot_status"),
                rs.getString("notes"),
                rs.getString("created_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("updated_by"),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
