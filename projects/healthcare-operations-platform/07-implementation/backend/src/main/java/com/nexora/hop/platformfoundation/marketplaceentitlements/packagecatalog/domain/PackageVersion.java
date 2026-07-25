package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * PackageVersion entity of AGG-030 MarketplacePackage. INV-MKT-001: a version cannot transition to
 * {@link #STATUS_PUBLISHED} unless {@code compatibilityApproved}, {@code securityReviewApproved},
 * {@code supportModelApproved} and {@code telemetryModelApproved} are all {@code true}
 * (RN-MKT-001, RN-MKT-004).
 *
 * <p>{@code compatibilityMetadataText} is nullable, delimited declared-compatibility metadata
 * (mirroring the {@code capabilityMappingsText}/{@code tierCodesText} pattern already used
 * elsewhere for multi-value fields), parsed by {@code CompatibilityMetadata.parse} and consumed by
 * {@code CompatibilityEvaluator} for compatibility.md's dimensions beyond {@code platform_version}
 * (COM-MOD-017-BE-002). A version with no declared metadata is only evaluated on {@code
 * platform_version}, unchanged from COM-MOD-017-BE-001 behavior.
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
        String compatibilityMetadataText,
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
