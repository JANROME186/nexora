package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.adapter.out.jdbc;

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
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequest;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequestRepository;

@Repository
@Profile("local")
class JdbcQuotationRequestRepository implements QuotationRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcQuotationRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public QuotationRequest save(QuotationRequest quotation) {
        jdbcTemplate.update("""
                insert into care_delivery.quotations (
                    quotation_id, tenant_id, laboratory_id, branch_id, patient_id, prospective_full_name,
                    prospective_phone, prospective_email, price_list_id, price_list_version, total_amount,
                    total_currency, discount_kind, discount_value, valid_until, channel, status, converted_order_id,
                    cancellation_reason, actor_id, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (quotation_id) do update set
                    price_list_id = excluded.price_list_id,
                    price_list_version = excluded.price_list_version,
                    total_amount = excluded.total_amount,
                    total_currency = excluded.total_currency,
                    discount_kind = excluded.discount_kind,
                    discount_value = excluded.discount_value,
                    valid_until = excluded.valid_until,
                    channel = excluded.channel,
                    status = excluded.status,
                    converted_order_id = excluded.converted_order_id,
                    cancellation_reason = excluded.cancellation_reason,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                quotation.quotationId(), quotation.tenantId(), quotation.laboratoryId(), quotation.branchId(),
                quotation.patientId(), quotation.prospectiveFullName(), quotation.prospectivePhone(),
                quotation.prospectiveEmail(), quotation.priceListId(),
                quotation.priceListId() == null ? null : quotation.priceListVersion(),
                quotation.totalAmount() == null ? null : quotation.totalAmount().amount(),
                quotation.totalAmount() == null ? null : quotation.totalAmount().currency(),
                quotation.discountKind(), quotation.discountValue(), sqlDate(quotation.validUntil()),
                quotation.channel(), quotation.status(), quotation.convertedOrderId(), quotation.cancellationReason(),
                quotation.actorId(), quotation.version(), Timestamp.from(quotation.createdAt()),
                Timestamp.from(quotation.updatedAt()));
        return quotation;
    }

    @Override
    public Optional<QuotationRequest> findById(String quotationId) {
        return jdbcTemplate.query("select * from care_delivery.quotations where quotation_id = ?",
                JdbcQuotationRequestRepository::map, quotationId).stream().findFirst();
    }

    @Override
    public List<QuotationRequest> findByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from care_delivery.quotations where tenant_id = ?",
                JdbcQuotationRequestRepository::map, tenantId);
    }

    @Override
    public QuotationLine saveLine(QuotationLine line) {
        jdbcTemplate.update("""
                insert into care_delivery.quotation_lines (
                    line_id, quotation_id, test_definition_id, catalog_item_kind, published_version,
                    quantity, unit_amount, unit_currency)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (line_id) do update set
                    unit_amount = excluded.unit_amount,
                    unit_currency = excluded.unit_currency
                """,
                line.lineId(), line.quotationId(), line.testDefinitionId(), line.catalogItemKind(),
                line.publishedVersion(), line.quantity(),
                line.unitAmount() == null ? null : line.unitAmount().amount(),
                line.unitAmount() == null ? null : line.unitAmount().currency());
        return line;
    }

    @Override
    public List<QuotationLine> findLines(String quotationId) {
        return jdbcTemplate.query("select * from care_delivery.quotation_lines where quotation_id = ?",
                JdbcQuotationRequestRepository::mapLine, quotationId);
    }

    private static QuotationRequest map(ResultSet resultSet, int rowNumber) throws SQLException {
        String priceListId = resultSet.getString("price_list_id");
        java.math.BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");
        return new QuotationRequest(
                resultSet.getString("quotation_id"), resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"), resultSet.getString("branch_id"),
                resultSet.getString("patient_id"), resultSet.getString("prospective_full_name"),
                resultSet.getString("prospective_phone"), resultSet.getString("prospective_email"),
                priceListId, resultSet.getInt("price_list_version"),
                totalAmount == null ? null : new Money(resultSet.getString("total_currency"), totalAmount),
                resultSet.getString("discount_kind"), resultSet.getBigDecimal("discount_value"),
                localDate(resultSet, "valid_until"), resultSet.getString("channel"), resultSet.getString("status"),
                resultSet.getString("converted_order_id"), resultSet.getString("cancellation_reason"),
                resultSet.getString("actor_id"), resultSet.getInt("version"),
                resultSet.getTimestamp("created_at").toInstant(), resultSet.getTimestamp("updated_at").toInstant());
    }

    private static QuotationLine mapLine(ResultSet resultSet, int rowNumber) throws SQLException {
        BigDecimal unitAmount = resultSet.getBigDecimal("unit_amount");
        return new QuotationLine(
                resultSet.getString("line_id"), resultSet.getString("quotation_id"),
                resultSet.getString("test_definition_id"), resultSet.getString("catalog_item_kind"),
                resultSet.getInt("published_version"), resultSet.getInt("quantity"),
                unitAmount == null ? null : new Money(resultSet.getString("unit_currency"), unitAmount));
    }

    private static Date sqlDate(LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }

    private static LocalDate localDate(ResultSet resultSet, String columnName) throws SQLException {
        Date value = resultSet.getDate(columnName);
        return value == null ? null : value.toLocalDate();
    }
}
