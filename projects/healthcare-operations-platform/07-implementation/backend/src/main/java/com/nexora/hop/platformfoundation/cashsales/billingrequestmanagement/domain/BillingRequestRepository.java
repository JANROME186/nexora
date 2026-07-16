package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface BillingRequestRepository {

    InvoiceRequest save(InvoiceRequest request);

    Optional<InvoiceRequest> findById(String invoiceRequestId);

    Optional<InvoiceRequest> findBySaleId(String saleId);

    List<InvoiceRequest> findByTenantId(String tenantId);

    TaxLine saveTaxLine(TaxLine line);

    List<TaxLine> findTaxLines(String invoiceRequestId);
}
