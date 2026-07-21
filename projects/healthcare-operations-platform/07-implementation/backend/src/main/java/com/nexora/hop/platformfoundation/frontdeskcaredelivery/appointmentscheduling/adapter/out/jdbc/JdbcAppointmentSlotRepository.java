package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.adapter.out.jdbc;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlotRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.RequestedCatalogItem;

@Repository
@Profile("local")
class JdbcAppointmentSlotRepository implements AppointmentSlotRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcAppointmentSlotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AppointmentSlot save(AppointmentSlot appointment) {
        jdbcTemplate.update("""
                insert into care_delivery.appointments (
                    appointment_id, tenant_id, laboratory_id, branch_id, patient_id, doctor_id,
                    scheduled_start, scheduled_end, channel, status, linked_order_id, cancellation_reason,
                    actor_id, prospective_full_name, prospective_phone, prospective_email,
                    version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (appointment_id) do update set
                    status = excluded.status,
                    linked_order_id = excluded.linked_order_id,
                    cancellation_reason = excluded.cancellation_reason,
                    prospective_full_name = excluded.prospective_full_name,
                    prospective_phone = excluded.prospective_phone,
                    prospective_email = excluded.prospective_email,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                appointment.appointmentId(), appointment.tenantId(), appointment.laboratoryId(),
                appointment.branchId(), appointment.patientId(), appointment.doctorId(),
                Date.valueOf(appointment.scheduledStart()), Date.valueOf(appointment.scheduledEnd()),
                appointment.channel(), appointment.status(), appointment.linkedOrderId(),
                appointment.cancellationReason(), appointment.actorId(),
                appointment.prospectiveFullName(), appointment.prospectivePhone(), appointment.prospectiveEmail(),
                appointment.version(),
                Timestamp.from(appointment.createdAt()), Timestamp.from(appointment.updatedAt()));
        return appointment;
    }

    @Override
    public Optional<AppointmentSlot> findById(String appointmentId) {
        return jdbcTemplate.query("select * from care_delivery.appointments where appointment_id = ?",
                JdbcAppointmentSlotRepository::map, appointmentId).stream().findFirst();
    }

    @Override
    public List<AppointmentSlot> findByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from care_delivery.appointments where tenant_id = ?",
                JdbcAppointmentSlotRepository::map, tenantId);
    }

    @Override
    public List<AppointmentSlot> findByPatientAndBranch(String patientId, String branchId) {
        return jdbcTemplate.query(
                "select * from care_delivery.appointments where patient_id = ? and branch_id = ?",
                JdbcAppointmentSlotRepository::map, patientId, branchId);
    }

    @Override
    public List<AppointmentSlot> findByBranchId(String branchId) {
        return jdbcTemplate.query("select * from care_delivery.appointments where branch_id = ?",
                JdbcAppointmentSlotRepository::map, branchId);
    }

    @Override
    public RequestedCatalogItem saveRequestedItem(RequestedCatalogItem item) {
        jdbcTemplate.update("""
                insert into care_delivery.appointment_requested_items (
                    item_id, appointment_id, test_definition_id, catalog_item_kind)
                values (?, ?, ?, ?)
                on conflict (item_id) do nothing
                """,
                item.itemId(), item.appointmentId(), item.testDefinitionId(), item.catalogItemKind());
        return item;
    }

    @Override
    public List<RequestedCatalogItem> findRequestedItems(String appointmentId) {
        return jdbcTemplate.query(
                "select * from care_delivery.appointment_requested_items where appointment_id = ?",
                (resultSet, rowNumber) -> new RequestedCatalogItem(
                        resultSet.getString("item_id"), resultSet.getString("appointment_id"),
                        resultSet.getString("test_definition_id"), resultSet.getString("catalog_item_kind")),
                appointmentId);
    }

    private static AppointmentSlot map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new AppointmentSlot(
                resultSet.getString("appointment_id"), resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"), resultSet.getString("branch_id"),
                resultSet.getString("patient_id"), resultSet.getString("doctor_id"),
                localDate(resultSet, "scheduled_start"), localDate(resultSet, "scheduled_end"),
                resultSet.getString("channel"), resultSet.getString("status"),
                resultSet.getString("linked_order_id"), resultSet.getString("cancellation_reason"),
                resultSet.getString("actor_id"),
                resultSet.getString("prospective_full_name"),
                resultSet.getString("prospective_phone"),
                resultSet.getString("prospective_email"),
                resultSet.getInt("version"),
                resultSet.getTimestamp("created_at").toInstant(), resultSet.getTimestamp("updated_at").toInstant());
    }

    private static LocalDate localDate(ResultSet resultSet, String columnName) throws SQLException {
        Date value = resultSet.getDate(columnName);
        return value == null ? null : value.toLocalDate();
    }
}
