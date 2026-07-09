package com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/**
 * Diagnostic Service Catalog entry (BCM-SVC-001), a sellable and orderable catalog entry
 * composed of tests and panels. Modeled in
 * bcm-svc-001-diagnostic-service-catalog/business-model.yaml (ENT-SVC-001).
 */
public record DiagnosticService(
        String serviceId,
        String tenantId,
        String laboratoryId,
        String code,
        LocalizedText name,
        String categoryId,
        String serviceType,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";

    public static final String TYPE_TEST = "test";
    public static final String TYPE_PANEL = "panel";
    public static final String TYPE_PROFILE = "profile";
    public static final String TYPE_MIXED = "mixed";
}
