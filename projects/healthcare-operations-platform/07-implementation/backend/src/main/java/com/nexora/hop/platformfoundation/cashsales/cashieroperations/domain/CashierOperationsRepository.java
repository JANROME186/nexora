package com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain;

import java.util.List;
import java.util.Optional;

public interface CashierOperationsRepository {

    CashSession saveSession(CashSession session);

    Optional<CashSession> findSessionById(String sessionId);

    Optional<CashSession> findOpenSession(String tenantId, String branchId);

    List<CashSession> findSessionsByTenantId(String tenantId);

    Sale saveSale(Sale sale);

    Optional<Sale> findSaleById(String saleId);

    Optional<Sale> findSaleBySource(String sourceType, String sourceReferenceId);

    List<Sale> findSalesByTenantId(String tenantId);

    SaleLine saveSaleLine(SaleLine line);

    List<SaleLine> findSaleLines(String saleId);

    PaymentAllocation savePayment(PaymentAllocation payment);

    List<PaymentAllocation> findPayments(String saleId);
}
