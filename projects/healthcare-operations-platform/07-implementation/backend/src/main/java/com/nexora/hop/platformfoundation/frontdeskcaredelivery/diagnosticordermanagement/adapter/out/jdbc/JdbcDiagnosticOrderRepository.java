package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.adapter.out.jdbc;

import java.math.BigDecimal;
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

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.BranchSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrderRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DoctorSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderPricingSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.PatientSnapshot;

@Repository
@Profile("local")
class JdbcDiagnosticOrderRepository implements DiagnosticOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcDiagnosticOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DiagnosticOrder save(DiagnosticOrder order) {
        jdbcTemplate.update("""
                insert into care_delivery.diagnostic_orders (
                    order_id, tenant_id, laboratory_id, branch_id, intake_channel, source_reference_id,
                    patient_id, patient_snapshot_version, patient_full_name, patient_document_type,
                    patient_document_number_masked, patient_birth_date, patient_captured_at,
                    doctor_id, doctor_snapshot_version, doctor_full_name, doctor_license_number, doctor_captured_at,
                    branch_snapshot_version, branch_name, branch_captured_at,
                    clinical_notes, price_list_id, price_list_version, total_amount, total_currency,
                    pricing_captured_at, status, cancellation_reason, actor_id, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (order_id) do update set
                    clinical_notes = excluded.clinical_notes,
                    price_list_id = excluded.price_list_id,
                    price_list_version = excluded.price_list_version,
                    total_amount = excluded.total_amount,
                    total_currency = excluded.total_currency,
                    pricing_captured_at = excluded.pricing_captured_at,
                    status = excluded.status,
                    cancellation_reason = excluded.cancellation_reason,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                order.orderId(), order.tenantId(), order.laboratoryId(), order.branchId(), order.intakeChannel(),
                order.sourceReferenceId(),
                order.patientSnapshot().patientId(), order.patientSnapshot().sourceVersion(),
                order.patientSnapshot().fullName(), order.patientSnapshot().documentType(),
                order.patientSnapshot().documentNumberMasked(), sqlDate(order.patientSnapshot().birthDate()),
                Timestamp.from(order.patientSnapshot().capturedAt()),
                order.doctorSnapshot() == null ? null : order.doctorSnapshot().doctorId(),
                order.doctorSnapshot() == null ? null : order.doctorSnapshot().sourceVersion(),
                order.doctorSnapshot() == null ? null : order.doctorSnapshot().fullName(),
                order.doctorSnapshot() == null ? null : order.doctorSnapshot().licenseNumber(),
                order.doctorSnapshot() == null ? null : Timestamp.from(order.doctorSnapshot().capturedAt()),
                order.branchSnapshot().sourceVersion(), order.branchSnapshot().name(),
                Timestamp.from(order.branchSnapshot().capturedAt()),
                order.clinicalNotes(),
                order.pricingSnapshot() == null ? null : order.pricingSnapshot().priceListId(),
                order.pricingSnapshot() == null ? null : order.pricingSnapshot().priceListVersion(),
                order.pricingSnapshot() == null ? null : order.pricingSnapshot().totalAmount().amount(),
                order.pricingSnapshot() == null ? null : order.pricingSnapshot().totalAmount().currency(),
                order.pricingSnapshot() == null ? null : Timestamp.from(order.pricingSnapshot().capturedAt()),
                order.status(), order.cancellationReason(), order.actorId(), order.version(),
                Timestamp.from(order.createdAt()), Timestamp.from(order.updatedAt()));
        return order;
    }

    @Override
    public Optional<DiagnosticOrder> findById(String orderId) {
        return jdbcTemplate.query("select * from care_delivery.diagnostic_orders where order_id = ?",
                JdbcDiagnosticOrderRepository::mapOrder, orderId).stream().findFirst();
    }

    @Override
    public List<DiagnosticOrder> findByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from care_delivery.diagnostic_orders where tenant_id = ?",
                JdbcDiagnosticOrderRepository::mapOrder, tenantId);
    }

    @Override
    public OrderLine saveOrderLine(OrderLine line) {
        jdbcTemplate.update("""
                insert into care_delivery.diagnostic_order_lines (
                    order_line_id, order_id, test_definition_id, catalog_item_kind, catalog_item_name,
                    catalog_published_version, quantity, unit_amount, unit_currency, line_status)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (order_line_id) do update set
                    unit_amount = excluded.unit_amount,
                    unit_currency = excluded.unit_currency,
                    line_status = excluded.line_status
                """,
                line.orderLineId(), line.orderId(), line.testDefinitionId(), line.catalogItemKind(),
                line.catalogItemName(), line.catalogPublishedVersion(), line.quantity(),
                line.unitAmount() == null ? null : line.unitAmount().amount(),
                line.unitAmount() == null ? null : line.unitAmount().currency(), line.lineStatus());
        return line;
    }

    @Override
    public List<OrderLine> findOrderLines(String orderId) {
        return jdbcTemplate.query("select * from care_delivery.diagnostic_order_lines where order_id = ?",
                JdbcDiagnosticOrderRepository::mapOrderLine, orderId);
    }

    private static DiagnosticOrder mapOrder(ResultSet resultSet, int rowNumber) throws SQLException {
        String doctorId = resultSet.getString("doctor_id");
        DoctorSnapshot doctorSnapshot = doctorId == null ? null : new DoctorSnapshot(
                doctorId, resultSet.getInt("doctor_snapshot_version"), resultSet.getString("doctor_full_name"),
                resultSet.getString("doctor_license_number"),
                resultSet.getTimestamp("doctor_captured_at").toInstant());
        String priceListId = resultSet.getString("price_list_id");
        OrderPricingSnapshot pricingSnapshot = priceListId == null ? null : new OrderPricingSnapshot(
                priceListId, resultSet.getInt("price_list_version"),
                new Money(resultSet.getString("total_currency"), resultSet.getBigDecimal("total_amount")),
                resultSet.getTimestamp("pricing_captured_at").toInstant());
        return new DiagnosticOrder(
                resultSet.getString("order_id"), resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"), resultSet.getString("branch_id"),
                resultSet.getString("intake_channel"), resultSet.getString("source_reference_id"),
                new PatientSnapshot(resultSet.getString("patient_id"), resultSet.getInt("patient_snapshot_version"),
                        resultSet.getString("patient_full_name"), resultSet.getString("patient_document_type"),
                        resultSet.getString("patient_document_number_masked"),
                        localDate(resultSet, "patient_birth_date"),
                        resultSet.getTimestamp("patient_captured_at").toInstant()),
                doctorSnapshot,
                new BranchSnapshot(resultSet.getString("branch_id"), resultSet.getInt("branch_snapshot_version"),
                        resultSet.getString("branch_name"), resultSet.getTimestamp("branch_captured_at").toInstant()),
                resultSet.getString("clinical_notes"), pricingSnapshot, resultSet.getString("status"),
                resultSet.getString("cancellation_reason"), resultSet.getString("actor_id"),
                resultSet.getInt("version"), resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getTimestamp("updated_at").toInstant());
    }

    private static OrderLine mapOrderLine(ResultSet resultSet, int rowNumber) throws SQLException {
        BigDecimal unitAmount = resultSet.getBigDecimal("unit_amount");
        return new OrderLine(
                resultSet.getString("order_line_id"), resultSet.getString("order_id"),
                resultSet.getString("test_definition_id"), resultSet.getString("catalog_item_kind"),
                resultSet.getString("catalog_item_name"), resultSet.getInt("catalog_published_version"),
                resultSet.getInt("quantity"),
                unitAmount == null ? null : new Money(resultSet.getString("unit_currency"), unitAmount),
                resultSet.getString("line_status"));
    }

    private static Date sqlDate(LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }

    private static LocalDate localDate(ResultSet resultSet, String columnName) throws SQLException {
        Date value = resultSet.getDate(columnName);
        return value == null ? null : value.toLocalDate();
    }
}
