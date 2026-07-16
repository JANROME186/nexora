package com.nexora.hop.platformfoundation.frontdeskcaredelivery.application;

import java.util.List;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequest;

/**
 * Stable public port that the CashSales bounded context uses to read diagnostic order and quotation
 * source snapshots. Exposes only the read-only query surface required to create a Sale; it does not
 * allow CashSales to mutate any front-desk aggregate. Closes TD-BE-011.
 *
 * <p>Implementations must not propagate internal front-desk domain exceptions beyond this boundary;
 * callers receive a {@link FrontDeskSourceNotFoundException} for unknown identifiers.</p>
 */
public interface FrontDeskSaleSourcePort {

    /**
     * Returns an accepted or completed DiagnosticOrder by its identifier.
     *
     * @param orderId the diagnostic order identifier
     * @return the matching order aggregate
     * @throws FrontDeskSourceNotFoundException if no order exists with the given identifier
     */
    DiagnosticOrder findOrderById(String orderId);

    /**
     * Returns the line items for a diagnostic order.
     *
     * @param orderId the diagnostic order identifier
     * @return ordered list of order lines; never null
     */
    List<OrderLine> findOrderLines(String orderId);

    /**
     * Returns an accepted or converted QuotationRequest by its identifier.
     *
     * @param quotationId the quotation identifier
     * @return the matching quotation aggregate
     * @throws FrontDeskSourceNotFoundException if no quotation exists with the given identifier
     */
    QuotationRequest findQuotationById(String quotationId);

    /**
     * Returns the line items for a quotation.
     *
     * @param quotationId the quotation identifier
     * @return ordered list of quotation lines; never null
     */
    List<QuotationLine> findQuotationLines(String quotationId);
}
