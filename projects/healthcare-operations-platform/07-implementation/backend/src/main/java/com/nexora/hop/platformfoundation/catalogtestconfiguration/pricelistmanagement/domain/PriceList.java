package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain;

import java.time.Instant;
import java.time.LocalDate;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/**
 * A version-aware collection of price entries (BCM-SVC-009).
 * Modeled in bcm-svc-009-price-list-management/business-model.yaml (ENT-PRC-001).
 */
public record PriceList(
        String priceListId,
        String tenantId,
        String laboratoryId,
        String code,
        LocalizedText name,
        String currency,
        String agreementRefId,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";
}
