package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain;

import java.util.List;
import java.util.Optional;

public interface DiagnosticOrderRepository {

    DiagnosticOrder save(DiagnosticOrder order);

    Optional<DiagnosticOrder> findById(String orderId);

    List<DiagnosticOrder> findByTenantId(String tenantId);

    OrderLine saveOrderLine(OrderLine line);

    List<OrderLine> findOrderLines(String orderId);
}
