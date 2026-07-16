package com.nexora.hop.platformfoundation.cashsales.cashieroperations.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.CashierOperationsRepository;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.CashSession;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.PaymentAllocation;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.Sale;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.SaleLine;

@Repository
@Profile("!local")
class InMemoryCashierOperationsRepository implements CashierOperationsRepository {

    private final Map<String, CashSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Sale> sales = new ConcurrentHashMap<>();
    private final Map<String, SaleLine> saleLines = new ConcurrentHashMap<>();
    private final Map<String, PaymentAllocation> payments = new ConcurrentHashMap<>();

    @Override
    public CashSession saveSession(CashSession session) {
        sessions.put(session.sessionId(), session);
        return session;
    }

    @Override
    public Optional<CashSession> findSessionById(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public Optional<CashSession> findOpenSession(String tenantId, String branchId) {
        return sessions.values().stream()
                .filter(session -> tenantId.equals(session.tenantId())
                        && branchId.equals(session.branchId())
                        && CashSession.STATUS_OPEN.equals(session.status()))
                .findFirst();
    }

    @Override
    public List<CashSession> findSessionsByTenantId(String tenantId) {
        return sessions.values().stream().filter(session -> tenantId.equals(session.tenantId())).toList();
    }

    @Override
    public Sale saveSale(Sale sale) {
        sales.put(sale.saleId(), sale);
        return sale;
    }

    @Override
    public Optional<Sale> findSaleById(String saleId) {
        return Optional.ofNullable(sales.get(saleId));
    }

    @Override
    public Optional<Sale> findSaleBySource(String sourceType, String sourceReferenceId) {
        return sales.values().stream()
                .filter(sale -> sourceType.equals(sale.sourceType())
                        && sourceReferenceId.equals(sale.sourceReferenceId()))
                .findFirst();
    }

    @Override
    public List<Sale> findSalesByTenantId(String tenantId) {
        return sales.values().stream().filter(sale -> tenantId.equals(sale.tenantId())).toList();
    }

    @Override
    public SaleLine saveSaleLine(SaleLine line) {
        saleLines.put(line.saleLineId(), line);
        return line;
    }

    @Override
    public List<SaleLine> findSaleLines(String saleId) {
        return saleLines.values().stream().filter(line -> saleId.equals(line.saleId())).toList();
    }

    @Override
    public PaymentAllocation savePayment(PaymentAllocation payment) {
        payments.put(payment.paymentId(), payment);
        return payment;
    }

    @Override
    public List<PaymentAllocation> findPayments(String saleId) {
        return payments.values().stream().filter(payment -> saleId.equals(payment.saleId())).toList();
    }
}
