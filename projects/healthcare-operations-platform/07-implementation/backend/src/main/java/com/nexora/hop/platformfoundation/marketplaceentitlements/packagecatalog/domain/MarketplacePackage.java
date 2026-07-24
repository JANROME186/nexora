package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain;

import java.util.List;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Root aggregate of AGG-030 MarketplacePackage (BCM-PLT-011). Published package metadata and
 * lifecycle state; version-level catalog and certification state lives in {@link PackageVersion}.
 * RN-MKT-001: {@code capabilityMappings} must never be empty at submission time.
 */
public record MarketplacePackage(
        String packageId,
        String code,
        String name,
        String category,
        List<String> capabilityMappings,
        String status,
        AuditMetadata audit) {

    public static final String STATUS_SUBMITTED = "submitted";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_RETIRED = "retired";
}
