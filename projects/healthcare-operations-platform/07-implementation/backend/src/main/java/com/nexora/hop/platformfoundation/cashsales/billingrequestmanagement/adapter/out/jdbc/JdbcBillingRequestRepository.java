package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.BillingRequestRepository;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalProfileSnapshot;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.InvoiceRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.TaxLine;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

@Repository
@Profile("local")
class JdbcBillingRequestRepository implements BillingRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcBillingRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public InvoiceRequest save(InvoiceRequest request) {
        jdbcTemplate.update("""
                insert into cash_sales.invoice_requests (
                    invoice_request_id, tenant_id, laboratory_id, branch_id, sale_id, patient_id,
                    fiscal_legal_name, fiscal_tax_identifier, fiscal_address, fiscal_regime,
                    fiscal_captured_at, status, adapter_correlation_id, adapter_response_snapshot,
                    actor_id, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (invoice_request_id) do update set
                    status = excluded.status,
                    adapter_correlation_id = excluded.adapter_correlation_id,
                    adapter_response_snapshot = excluded.adapter_response_snapshot,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                request.invoiceRequestId(), request.tenantId(), request.laboratoryId(), request.branchId(),
                request.saleId(), request.patientId(), request.fiscalProfileSnapshot().legalName(),
                request.fiscalProfileSnapshot().taxIdentifier(), request.fiscalProfileSnapshot().fiscalAddress(),
                request.fiscalProfileSnapshot().fiscalRegime(),
                Timestamp.from(request.fiscalProfileSnapshot().capturedAt()), request.status(),
                request.adapterCorrelationId(), request.adapterResponseSnapshot(), request.actorId(),
                request.version(), Timestamp.from(request.createdAt()), Timestamp.from(request.updatedAt()));
        return request;
    }

    @Override
    public Optional<InvoiceRequest> findById(String invoiceRequestId) {
        return jdbcTemplate.query("select * from cash_sales.invoice_requests where invoice_request_id = ?",
                JdbcBillingRequestRepository::mapRequest, invoiceRequestId).stream().findFirst();
    }

    @Override
    public Optional<InvoiceRequest> findBySaleId(String saleId) {
        return jdbcTemplate.query("select * from cash_sales.invoice_requests where sale_id = ?",
                JdbcBillingRequestRepository::mapRequest, saleId).stream().findFirst();
    }

    @Override
    public List<InvoiceRequest> findByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from cash_sales.invoice_requests where tenant_id = ?",
                JdbcBillingRequestRepository::mapRequest, tenantId);
    }

    @Override
    public TaxLine saveTaxLine(TaxLine line) {
        jdbcTemplate.update("""
                insert into cash_sales.invoice_tax_lines (
                    tax_line_id, invoice_request_id, base_amount, base_currency, tax_code,
                    tax_rate, tax_amount, tax_currency)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (tax_line_id) do update set
                    tax_rate = excluded.tax_rate,
                    tax_amount = excluded.tax_amount,
                    tax_currency = excluded.tax_currency
                """,
                line.taxLineId(), line.invoiceRequestId(), line.baseAmount().amount(),
                line.baseAmount().currency(), line.taxCode(), line.taxRate(),
                line.taxAmount().amount(), line.taxAmount().currency());
        return line;
    }

    @Override
    public List<TaxLine> findTaxLines(String invoiceRequestId) {
        return jdbcTemplate.query("select * from cash_sales.invoice_tax_lines where invoice_request_id = ?",
                JdbcBillingRequestRepository::mapTaxLine, invoiceRequestId);
    }

    private static InvoiceRequest mapRequest(ResultSet rs, int rowNumber) throws SQLException {
        FiscalProfileSnapshot fiscalProfile = new FiscalProfileSnapshot(
                rs.getString("fiscal_legal_name"), rs.getString("fiscal_tax_identifier"),
                rs.getString("fiscal_address"), rs.getString("fiscal_regime"),
                rs.getTimestamp("fiscal_captured_at").toInstant());
        return new InvoiceRequest(rs.getString("invoice_request_id"), rs.getString("tenant_id"),
                rs.getString("laboratory_id"), rs.getString("branch_id"), rs.getString("sale_id"),
                rs.getString("patient_id"), fiscalProfile, rs.getString("status"),
                rs.getString("adapter_correlation_id"), rs.getString("adapter_response_snapshot"),
                rs.getString("actor_id"), rs.getInt("version"),
                rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant());
    }

    private static TaxLine mapTaxLine(ResultSet rs, int rowNumber) throws SQLException {
        return new TaxLine(rs.getString("tax_line_id"), rs.getString("invoice_request_id"),
                new Money(rs.getString("base_currency"), rs.getBigDecimal("base_amount")),
                rs.getString("tax_code"), rs.getBigDecimal("tax_rate"),
                new Money(rs.getString("tax_currency"), rs.getBigDecimal("tax_amount")));
    }
}
