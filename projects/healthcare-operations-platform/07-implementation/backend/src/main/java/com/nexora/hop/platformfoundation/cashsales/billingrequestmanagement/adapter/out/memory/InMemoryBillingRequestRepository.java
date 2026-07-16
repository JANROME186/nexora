package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.BillingRequestRepository;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.InvoiceRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.TaxLine;

@Repository
@Profile("!local")
class InMemoryBillingRequestRepository implements BillingRequestRepository {

    private final Map<String, InvoiceRequest> requests = new ConcurrentHashMap<>();
    private final Map<String, TaxLine> taxLines = new ConcurrentHashMap<>();

    @Override
    public InvoiceRequest save(InvoiceRequest request) {
        requests.put(request.invoiceRequestId(), request);
        return request;
    }

    @Override
    public Optional<InvoiceRequest> findById(String invoiceRequestId) {
        return Optional.ofNullable(requests.get(invoiceRequestId));
    }

    @Override
    public Optional<InvoiceRequest> findBySaleId(String saleId) {
        return requests.values().stream().filter(request -> saleId.equals(request.saleId())).findFirst();
    }

    @Override
    public List<InvoiceRequest> findByTenantId(String tenantId) {
        return requests.values().stream().filter(request -> tenantId.equals(request.tenantId())).toList();
    }

    @Override
    public TaxLine saveTaxLine(TaxLine line) {
        taxLines.put(line.taxLineId(), line);
        return line;
    }

    @Override
    public List<TaxLine> findTaxLines(String invoiceRequestId) {
        return taxLines.values().stream().filter(line -> invoiceRequestId.equals(line.invoiceRequestId())).toList();
    }
}
