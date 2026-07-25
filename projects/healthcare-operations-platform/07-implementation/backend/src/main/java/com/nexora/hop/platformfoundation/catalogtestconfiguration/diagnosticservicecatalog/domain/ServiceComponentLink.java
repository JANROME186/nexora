package com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain;

/**
 * Reference from a diagnostic service to a composing test or panel.
 * Modeled in bcm-svc-001-diagnostic-service-catalog/business-model.md (ENT-SVC-003).
 */
public record ServiceComponentLink(
        String linkId,
        String serviceId,
        String componentType,
        String componentRefId,
        Integer displayOrder) {

    public static final String COMPONENT_TEST = "test";
    public static final String COMPONENT_PANEL = "panel";
}
