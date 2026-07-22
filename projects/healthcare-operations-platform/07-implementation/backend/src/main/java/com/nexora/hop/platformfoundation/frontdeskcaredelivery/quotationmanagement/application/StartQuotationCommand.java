package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application;

import java.util.List;

public record StartQuotationCommand(
        String tenantId,
        String laboratoryId,
        String branchId,
        String patientId,
        String prospectiveFullName,
        String prospectivePhone,
        String prospectiveEmail,
        String channel,
        String actorId,
        List<QuotationLineInput> lines) {

    public record QuotationLineInput(String testDefinitionId, String catalogItemKind, Integer quantity) {
    }
}
