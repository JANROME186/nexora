package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface QuotationRequestRepository {

    QuotationRequest save(QuotationRequest quotation);

    Optional<QuotationRequest> findById(String quotationId);

    List<QuotationRequest> findByTenantId(String tenantId);

    QuotationLine saveLine(QuotationLine line);

    List<QuotationLine> findLines(String quotationId);
}
