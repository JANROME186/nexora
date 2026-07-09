package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/**
 * A clinical grouping of tests ordered together (BCM-SVC-003).
 * Modeled in bcm-svc-003-panel-catalog/business-model.yaml (ENT-PNL-001).
 */
public record PanelDefinition(
        String panelId,
        String tenantId,
        String laboratoryId,
        String code,
        LocalizedText name,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";
}
