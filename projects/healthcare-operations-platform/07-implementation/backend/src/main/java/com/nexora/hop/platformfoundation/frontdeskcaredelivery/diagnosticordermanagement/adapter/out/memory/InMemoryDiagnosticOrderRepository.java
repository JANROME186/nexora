package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrderRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderLine;

@Repository
@Profile("!local")
class InMemoryDiagnosticOrderRepository implements DiagnosticOrderRepository {

    private final Map<String, DiagnosticOrder> orders = new ConcurrentHashMap<>();
    private final Map<String, OrderLine> orderLines = new ConcurrentHashMap<>();

    @Override
    public DiagnosticOrder save(DiagnosticOrder order) {
        orders.put(order.orderId(), order);
        return order;
    }

    @Override
    public Optional<DiagnosticOrder> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public List<DiagnosticOrder> findByTenantId(String tenantId) {
        return orders.values().stream().filter(order -> order.tenantId().equals(tenantId)).toList();
    }

    @Override
    public OrderLine saveOrderLine(OrderLine line) {
        orderLines.put(line.orderLineId(), line);
        return line;
    }

    @Override
    public List<OrderLine> findOrderLines(String orderId) {
        return orderLines.values().stream().filter(line -> line.orderId().equals(orderId)).toList();
    }
}
