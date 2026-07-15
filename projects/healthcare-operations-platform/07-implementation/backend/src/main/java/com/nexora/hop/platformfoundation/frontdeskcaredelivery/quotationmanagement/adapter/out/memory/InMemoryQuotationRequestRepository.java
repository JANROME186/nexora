package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequest;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequestRepository;

@Repository
@Profile("!local")
class InMemoryQuotationRequestRepository implements QuotationRequestRepository {

    private final Map<String, QuotationRequest> quotations = new ConcurrentHashMap<>();
    private final Map<String, QuotationLine> lines = new ConcurrentHashMap<>();

    @Override
    public QuotationRequest save(QuotationRequest quotation) {
        quotations.put(quotation.quotationId(), quotation);
        return quotation;
    }

    @Override
    public Optional<QuotationRequest> findById(String quotationId) {
        return Optional.ofNullable(quotations.get(quotationId));
    }

    @Override
    public List<QuotationRequest> findByTenantId(String tenantId) {
        return quotations.values().stream().filter(q -> q.tenantId().equals(tenantId)).toList();
    }

    @Override
    public QuotationLine saveLine(QuotationLine line) {
        lines.put(line.lineId(), line);
        return line;
    }

    @Override
    public List<QuotationLine> findLines(String quotationId) {
        return lines.values().stream().filter(l -> l.quotationId().equals(quotationId)).toList();
    }
}
