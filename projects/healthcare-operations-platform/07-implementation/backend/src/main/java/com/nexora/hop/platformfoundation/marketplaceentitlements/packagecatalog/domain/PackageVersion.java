package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * PackageVersion entity of AGG-030 MarketplacePackage. INV-MKT-001: a version cannot transition to
 * {@link #STATUS_PUBLISHED} unless {@code compatibilityApproved}, {@code securityReviewApproved},
 * {@code supportModelApproved} and {@code telemetryModelApproved} are all {@code true}
 * (RN-MKT-001, RN-MKT-004).
 */
public record PackageVersion(
        String versionId,
        String packageId,
        String version,
        String lifecycleStatus,
        boolean compatibilityApproved,
        boolean securityReviewApproved,
        boolean supportModelApproved,
        boolean telemetryModelApproved,
        AuditMetadata audit) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_CERTIFIED = "certified";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_RETIRED = "retired";

    /** Certification gate required before publication (INV-MKT-001, RN-MKT-004). */
    public boolean isReadyForPublication() {
        return compatibilityApproved && securityReviewApproved && supportModelApproved && telemetryModelApproved;
    }
}
