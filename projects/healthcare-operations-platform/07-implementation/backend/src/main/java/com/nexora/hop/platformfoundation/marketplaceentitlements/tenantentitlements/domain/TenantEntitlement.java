package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain;

import java.time.LocalDateTime;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Root aggregate of AGG-032 TenantEntitlement (BCM-PLT-011). Tenant package access grant, validity
 * window and revocation state. {@code offerId} is nullable because an entitlement may be granted
 * directly by a marketplace operator without a preceding commercial offer acceptance.
 */
public record TenantEntitlement(
        String entitlementId,
        String tenantId,
        String packageId,
        String offerId,
        String status,
        LocalDateTime grantedAt,
        LocalDateTime expiresAt,
        String revokedReason,
        AuditMetadata audit) {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_REVOKED = "revoked";
    public static final String STATUS_EXPIRED = "expired";

    public boolean isActive(LocalDateTime now) {
        return STATUS_ACTIVE.equals(status) && (expiresAt == null || expiresAt.isAfter(now));
    }
}
