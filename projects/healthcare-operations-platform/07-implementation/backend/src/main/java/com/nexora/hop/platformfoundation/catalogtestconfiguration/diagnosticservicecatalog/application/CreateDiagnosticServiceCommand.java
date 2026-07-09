package com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.application;

import java.util.List;

public record CreateDiagnosticServiceCommand(
        String tenantId,
        String laboratoryId,
        String code,
        String nameEn,
        String nameEs,
        String categoryId,
        String serviceType,
        List<ServiceComponentLinkInput> components) {
}
