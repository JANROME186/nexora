package com.nexora.hop.platformfoundation.frontdeskcaredelivery.adapter.out.salesource;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.application.FrontDeskSaleSourcePort;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.application.FrontDeskSourceNotFoundException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.DiagnosticOrderManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application.QuotationManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequest;

/**
 * Default adapter that fulfils the {@link FrontDeskSaleSourcePort} contract by delegating to the
 * internal FrontDeskCareDelivery application services. Lives inside the frontdeskcaredelivery
 * module so it has unrestricted access to internal classes. CashSales only depends on the port
 * interface, never on this adapter directly.
 */
@Component
class FrontDeskSaleSourcePortAdapter implements FrontDeskSaleSourcePort {

    private final DiagnosticOrderManagementService diagnosticOrderService;
    private final QuotationManagementService quotationService;

    FrontDeskSaleSourcePortAdapter(
            DiagnosticOrderManagementService diagnosticOrderService,
            QuotationManagementService quotationService) {
        this.diagnosticOrderService = diagnosticOrderService;
        this.quotationService = quotationService;
    }

    @Override
    public DiagnosticOrder findOrderById(String orderId) {
        try {
            return diagnosticOrderService.get(orderId);
        } catch (Exception e) {
            throw new FrontDeskSourceNotFoundException("Diagnostic order not found: " + orderId);
        }
    }

    @Override
    public List<OrderLine> findOrderLines(String orderId) {
        return diagnosticOrderService.getOrderLines(orderId);
    }

    @Override
    public QuotationRequest findQuotationById(String quotationId) {
        try {
            return quotationService.get(quotationId);
        } catch (Exception e) {
            throw new FrontDeskSourceNotFoundException("Quotation not found: " + quotationId);
        }
    }

    @Override
    public List<QuotationLine> findQuotationLines(String quotationId) {
        return quotationService.getLines(quotationId);
    }
}
