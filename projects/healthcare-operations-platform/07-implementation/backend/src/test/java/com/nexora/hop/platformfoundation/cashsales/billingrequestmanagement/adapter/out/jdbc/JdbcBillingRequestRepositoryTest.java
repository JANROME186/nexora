package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.adapter.out.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalProfileSnapshot;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.InvoiceRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.TaxLine;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

class JdbcBillingRequestRepositoryTest {

    private static final Instant NOW = Instant.parse("2026-07-17T20:00:00Z");

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final JdbcBillingRequestRepository repository = new JdbcBillingRequestRepository(jdbcTemplate);

    @Test
    void savesInvoiceRequestAndTaxLineUsingJdbcTemplate() {
        InvoiceRequest request = invoiceRequest();
        TaxLine line = taxLine();

        assertThat(repository.save(request)).isSameAs(request);
        assertThat(repository.saveTaxLine(line)).isSameAs(line);

        verify(jdbcTemplate, times(2)).update(anyString(), any(Object[].class));
    }

    @Test
    void mapsInvoiceRequestQueriesFromResultSetRows() throws Exception {
        ResultSet resultSet = invoiceRequestRow();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenAnswer(invocation -> List.of(invocation.<RowMapper<InvoiceRequest>>getArgument(1).mapRow(resultSet, 0)));

        var byId = repository.findById("invoice-1");
        var bySale = repository.findBySaleId("sale-1");
        var byTenant = repository.findByTenantId("tenant-1");

        assertThat(byId).isPresent();
        assertThat(bySale).isPresent();
        assertThat(byTenant).hasSize(1);
        assertThat(byId.get().fiscalProfileSnapshot().taxIdentifier()).isEqualTo("RFC010101ABC");
        assertThat(byId.get().adapterResponseSnapshot()).isEqualTo("{\"status\":\"ok\"}");
    }

    @Test
    void mapsTaxLinesFromResultSetRows() throws Exception {
        ResultSet resultSet = taxLineRow();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenAnswer(invocation -> List.of(invocation.<RowMapper<TaxLine>>getArgument(1).mapRow(resultSet, 0)));

        List<TaxLine> lines = repository.findTaxLines("invoice-1");

        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).baseAmount()).isEqualTo(new Money("MXN", new BigDecimal("100.00")));
        assertThat(lines.get(0).taxAmount()).isEqualTo(new Money("MXN", new BigDecimal("16.00")));
    }

    private static InvoiceRequest invoiceRequest() {
        return new InvoiceRequest(
                "invoice-1",
                "tenant-1",
                "lab-1",
                "branch-1",
                "sale-1",
                "patient-1",
                new FiscalProfileSnapshot("Paciente Demo", "RFC010101ABC", "Calle 1", "601", NOW),
                InvoiceRequest.STATUS_REQUESTED,
                "adapter-1",
                "{\"status\":\"ok\"}",
                "actor-1",
                1,
                NOW,
                NOW);
    }

    private static TaxLine taxLine() {
        return new TaxLine(
                "tax-1",
                "invoice-1",
                new Money("MXN", new BigDecimal("100.00")),
                "IVA",
                new BigDecimal("0.16"),
                new Money("MXN", new BigDecimal("16.00")));
    }

    private static ResultSet invoiceRequestRow() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("invoice_request_id")).thenReturn("invoice-1");
        when(rs.getString("tenant_id")).thenReturn("tenant-1");
        when(rs.getString("laboratory_id")).thenReturn("lab-1");
        when(rs.getString("branch_id")).thenReturn("branch-1");
        when(rs.getString("sale_id")).thenReturn("sale-1");
        when(rs.getString("patient_id")).thenReturn("patient-1");
        when(rs.getString("fiscal_legal_name")).thenReturn("Paciente Demo");
        when(rs.getString("fiscal_tax_identifier")).thenReturn("RFC010101ABC");
        when(rs.getString("fiscal_address")).thenReturn("Calle 1");
        when(rs.getString("fiscal_regime")).thenReturn("601");
        when(rs.getTimestamp("fiscal_captured_at")).thenReturn(Timestamp.from(NOW));
        when(rs.getString("status")).thenReturn(InvoiceRequest.STATUS_REQUESTED);
        when(rs.getString("adapter_correlation_id")).thenReturn("adapter-1");
        when(rs.getString("adapter_response_snapshot")).thenReturn("{\"status\":\"ok\"}");
        when(rs.getString("actor_id")).thenReturn("actor-1");
        when(rs.getInt("version")).thenReturn(1);
        when(rs.getTimestamp("created_at")).thenReturn(Timestamp.from(NOW));
        when(rs.getTimestamp("updated_at")).thenReturn(Timestamp.from(NOW));
        return rs;
    }

    private static ResultSet taxLineRow() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("tax_line_id")).thenReturn("tax-1");
        when(rs.getString("invoice_request_id")).thenReturn("invoice-1");
        when(rs.getString("base_currency")).thenReturn("MXN");
        when(rs.getBigDecimal("base_amount")).thenReturn(new BigDecimal("100.00"));
        when(rs.getString("tax_code")).thenReturn("IVA");
        when(rs.getBigDecimal("tax_rate")).thenReturn(new BigDecimal("0.16"));
        when(rs.getString("tax_currency")).thenReturn("MXN");
        when(rs.getBigDecimal("tax_amount")).thenReturn(new BigDecimal("16.00"));
        return rs;
    }
}
