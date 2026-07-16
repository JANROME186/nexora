package com.nexora.hop.platformfoundation.cashsales.cashieroperations.adapter.out.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.CashierOperationsRepository;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.CashSession;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.PaymentAllocation;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.Sale;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.SaleLine;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.SaleTotals;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

@Repository
@Profile("local")
class JdbcCashierOperationsRepository implements CashierOperationsRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcCashierOperationsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CashSession saveSession(CashSession session) {
        jdbcTemplate.update("""
                insert into cash_sales.cash_sessions (
                    session_id, tenant_id, laboratory_id, branch_id, opened_by,
                    opening_amount, opening_currency, expected_amount, expected_currency,
                    counted_amount, counted_currency, variance_amount, variance_currency,
                    variance_reason, status, opened_at, closed_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (session_id) do update set
                    expected_amount = excluded.expected_amount,
                    expected_currency = excluded.expected_currency,
                    counted_amount = excluded.counted_amount,
                    counted_currency = excluded.counted_currency,
                    variance_amount = excluded.variance_amount,
                    variance_currency = excluded.variance_currency,
                    variance_reason = excluded.variance_reason,
                    status = excluded.status,
                    closed_at = excluded.closed_at
                """,
                session.sessionId(), session.tenantId(), session.laboratoryId(), session.branchId(),
                session.openedBy(), session.openingAmount().amount(), session.openingAmount().currency(),
                amount(session.expectedAmount()), currency(session.expectedAmount()),
                amount(session.countedAmount()), currency(session.countedAmount()),
                amount(session.varianceAmount()), currency(session.varianceAmount()),
                session.varianceReason(), session.status(), Timestamp.from(session.openedAt()),
                session.closedAt() == null ? null : Timestamp.from(session.closedAt()));
        return session;
    }

    @Override
    public Optional<CashSession> findSessionById(String sessionId) {
        return jdbcTemplate.query("select * from cash_sales.cash_sessions where session_id = ?",
                JdbcCashierOperationsRepository::mapSession, sessionId).stream().findFirst();
    }

    @Override
    public Optional<CashSession> findOpenSession(String tenantId, String branchId) {
        return jdbcTemplate.query("""
                select * from cash_sales.cash_sessions
                 where tenant_id = ? and branch_id = ? and status = ?
                """, JdbcCashierOperationsRepository::mapSession, tenantId, branchId, CashSession.STATUS_OPEN)
                .stream().findFirst();
    }

    @Override
    public List<CashSession> findSessionsByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from cash_sales.cash_sessions where tenant_id = ?",
                JdbcCashierOperationsRepository::mapSession, tenantId);
    }

    @Override
    public Sale saveSale(Sale sale) {
        jdbcTemplate.update("""
                insert into cash_sales.sales (
                    sale_id, tenant_id, laboratory_id, branch_id, patient_id, source_type, source_reference_id,
                    subtotal_amount, subtotal_currency, discount_amount, discount_currency, total_amount,
                    total_currency, paid_amount, paid_currency, outstanding_amount, outstanding_currency,
                    status, cancellation_reason, actor_id, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (sale_id) do update set
                    paid_amount = excluded.paid_amount,
                    paid_currency = excluded.paid_currency,
                    outstanding_amount = excluded.outstanding_amount,
                    outstanding_currency = excluded.outstanding_currency,
                    status = excluded.status,
                    cancellation_reason = excluded.cancellation_reason,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                sale.saleId(), sale.tenantId(), sale.laboratoryId(), sale.branchId(), sale.patientId(),
                sale.sourceType(), sale.sourceReferenceId(),
                sale.totals().subtotalAmount().amount(), sale.totals().subtotalAmount().currency(),
                sale.totals().discountAmount().amount(), sale.totals().discountAmount().currency(),
                sale.totals().totalAmount().amount(), sale.totals().totalAmount().currency(),
                sale.totals().paidAmount().amount(), sale.totals().paidAmount().currency(),
                sale.totals().outstandingAmount().amount(), sale.totals().outstandingAmount().currency(),
                sale.status(), sale.cancellationReason(), sale.actorId(), sale.version(),
                Timestamp.from(sale.createdAt()), Timestamp.from(sale.updatedAt()));
        return sale;
    }

    @Override
    public Optional<Sale> findSaleById(String saleId) {
        return jdbcTemplate.query("select * from cash_sales.sales where sale_id = ?",
                JdbcCashierOperationsRepository::mapSale, saleId).stream().findFirst();
    }

    @Override
    public Optional<Sale> findSaleBySource(String sourceType, String sourceReferenceId) {
        return jdbcTemplate.query("""
                select * from cash_sales.sales where source_type = ? and source_reference_id = ?
                """, JdbcCashierOperationsRepository::mapSale, sourceType, sourceReferenceId).stream().findFirst();
    }

    @Override
    public List<Sale> findSalesByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from cash_sales.sales where tenant_id = ?",
                JdbcCashierOperationsRepository::mapSale, tenantId);
    }

    @Override
    public SaleLine saveSaleLine(SaleLine line) {
        jdbcTemplate.update("""
                insert into cash_sales.sale_lines (
                    sale_line_id, sale_id, catalog_item_id, catalog_item_kind, description_snapshot,
                    quantity, unit_amount, unit_currency, line_total_amount, line_total_currency)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (sale_line_id) do update set
                    quantity = excluded.quantity,
                    unit_amount = excluded.unit_amount,
                    unit_currency = excluded.unit_currency,
                    line_total_amount = excluded.line_total_amount,
                    line_total_currency = excluded.line_total_currency
                """,
                line.saleLineId(), line.saleId(), line.catalogItemId(), line.catalogItemKind(),
                line.descriptionSnapshot(), line.quantity(), line.unitAmount().amount(), line.unitAmount().currency(),
                line.lineTotal().amount(), line.lineTotal().currency());
        return line;
    }

    @Override
    public List<SaleLine> findSaleLines(String saleId) {
        return jdbcTemplate.query("select * from cash_sales.sale_lines where sale_id = ?",
                JdbcCashierOperationsRepository::mapSaleLine, saleId);
    }

    @Override
    public PaymentAllocation savePayment(PaymentAllocation payment) {
        jdbcTemplate.update("""
                insert into cash_sales.payment_allocations (
                    payment_id, sale_id, session_id, amount, currency, method, reference, registered_by, registered_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                payment.paymentId(), payment.saleId(), payment.sessionId(), payment.amount().amount(),
                payment.amount().currency(), payment.method(), payment.reference(), payment.registeredBy(),
                Timestamp.from(payment.registeredAt()));
        return payment;
    }

    @Override
    public List<PaymentAllocation> findPayments(String saleId) {
        return jdbcTemplate.query("select * from cash_sales.payment_allocations where sale_id = ?",
                JdbcCashierOperationsRepository::mapPayment, saleId);
    }

    private static CashSession mapSession(ResultSet rs, int rowNumber) throws SQLException {
        return new CashSession(rs.getString("session_id"), rs.getString("tenant_id"),
                rs.getString("laboratory_id"), rs.getString("branch_id"), rs.getString("opened_by"),
                new Money(rs.getString("opening_currency"), rs.getBigDecimal("opening_amount")),
                money(rs, "expected"), money(rs, "counted"), money(rs, "variance"),
                rs.getString("variance_reason"), rs.getString("status"),
                rs.getTimestamp("opened_at").toInstant(),
                rs.getTimestamp("closed_at") == null ? null : rs.getTimestamp("closed_at").toInstant());
    }

    private static Sale mapSale(ResultSet rs, int rowNumber) throws SQLException {
        SaleTotals totals = new SaleTotals(
                new Money(rs.getString("subtotal_currency"), rs.getBigDecimal("subtotal_amount")),
                new Money(rs.getString("discount_currency"), rs.getBigDecimal("discount_amount")),
                new Money(rs.getString("total_currency"), rs.getBigDecimal("total_amount")),
                new Money(rs.getString("paid_currency"), rs.getBigDecimal("paid_amount")),
                new Money(rs.getString("outstanding_currency"), rs.getBigDecimal("outstanding_amount")));
        return new Sale(rs.getString("sale_id"), rs.getString("tenant_id"), rs.getString("laboratory_id"),
                rs.getString("branch_id"), rs.getString("patient_id"), rs.getString("source_type"),
                rs.getString("source_reference_id"), totals, rs.getString("status"),
                rs.getString("cancellation_reason"), rs.getString("actor_id"), rs.getInt("version"),
                rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant());
    }

    private static SaleLine mapSaleLine(ResultSet rs, int rowNumber) throws SQLException {
        return new SaleLine(rs.getString("sale_line_id"), rs.getString("sale_id"),
                rs.getString("catalog_item_id"), rs.getString("catalog_item_kind"),
                rs.getString("description_snapshot"), rs.getInt("quantity"),
                new Money(rs.getString("unit_currency"), rs.getBigDecimal("unit_amount")),
                new Money(rs.getString("line_total_currency"), rs.getBigDecimal("line_total_amount")));
    }

    private static PaymentAllocation mapPayment(ResultSet rs, int rowNumber) throws SQLException {
        return new PaymentAllocation(rs.getString("payment_id"), rs.getString("sale_id"),
                rs.getString("session_id"), new Money(rs.getString("currency"), rs.getBigDecimal("amount")),
                rs.getString("method"), rs.getString("reference"), rs.getString("registered_by"),
                rs.getTimestamp("registered_at").toInstant());
    }

    private static Money money(ResultSet rs, String prefix) throws SQLException {
        BigDecimal amount = rs.getBigDecimal(prefix + "_amount");
        String currency = rs.getString(prefix + "_currency");
        return amount == null ? null : new Money(currency, amount);
    }

    private static BigDecimal amount(Money money) {
        return money == null ? null : money.amount();
    }

    private static String currency(Money money) {
        return money == null ? null : money.currency();
    }
}
