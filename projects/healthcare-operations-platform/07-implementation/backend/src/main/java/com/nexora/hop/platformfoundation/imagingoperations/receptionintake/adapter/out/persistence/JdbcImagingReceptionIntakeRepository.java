package com.nexora.hop.platformfoundation.imagingoperations.receptionintake.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain.ImagingReceptionIntake;
import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain.ImagingReceptionIntakeRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
public class JdbcImagingReceptionIntakeRepository implements ImagingReceptionIntakeRepository {

    private static final String SELECT_SQL = """
            select intake_id, tenant_id, appointment_slot_id, patient_id, intake_time,
                   check_in_status, preparation_verified, intake_notes, created_by, created_at,
                   updated_by, updated_at
            from imaging_operations.imaging_reception_intakes
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcImagingReceptionIntakeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ImagingReceptionIntake save(ImagingReceptionIntake intake) {
        jdbcTemplate.update("""
                insert into imaging_operations.imaging_reception_intakes
                    (intake_id, tenant_id, appointment_slot_id, patient_id, intake_time,
                     check_in_status, preparation_verified, intake_notes, created_by, created_at,
                     updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (intake_id) do update set
                    check_in_status = excluded.check_in_status,
                    preparation_verified = excluded.preparation_verified,
                    intake_notes = excluded.intake_notes,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                intake.intakeId(), intake.tenantId(), intake.appointmentSlotId(), intake.patientId(),
                Timestamp.from(intake.intakeTime()), intake.checkInStatus(), intake.preparationVerified(),
                intake.intakeNotes(), intake.createdBy(), Timestamp.from(intake.createdAt()),
                intake.updatedBy(), Timestamp.from(intake.updatedAt()));
        return intake;
    }

    @Override
    public Optional<ImagingReceptionIntake> findById(String tenantId, String intakeId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and intake_id = ?",
                JdbcImagingReceptionIntakeRepository::map, tenantId, intakeId).stream().findFirst();
    }

    @Override
    public Optional<ImagingReceptionIntake> findByAppointmentSlotId(String tenantId, String appointmentSlotId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and appointment_slot_id = ?",
                JdbcImagingReceptionIntakeRepository::map, tenantId, appointmentSlotId).stream().findFirst();
    }

    private static ImagingReceptionIntake map(ResultSet rs, int rowNum) throws SQLException {
        return new ImagingReceptionIntake(
                rs.getString("intake_id"),
                rs.getString("tenant_id"),
                rs.getString("appointment_slot_id"),
                rs.getString("patient_id"),
                rs.getTimestamp("intake_time").toInstant(),
                rs.getString("check_in_status"),
                rs.getBoolean("preparation_verified"),
                rs.getString("intake_notes"),
                rs.getString("created_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("updated_by"),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
