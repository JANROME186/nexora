package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Root aggregate of AGG-033 PackageInstallation (BCM-PLT-011). Tenant installation lifecycle,
 * activation and rollback evidence (installation-model.yaml). {@code rollbackCheckpointVersion}
 * models {@code RollbackCheckpoint}: INV-MKT-004 requires it to be set before activation whenever
 * rollback is supported, and {@link #rollbackPackage} requires it non-null.
 */
public record PackageInstallation(
        String installationId,
        String tenantId,
        String packageId,
        String entitlementId,
        String version,
        String lifecycleStatus,
        String rollbackCheckpointVersion,
        AuditMetadata audit) {

    public static final String STATUS_INSTALLED = "installed";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_SUSPENDED = "suspended";
    public static final String STATUS_UNINSTALLED = "uninstalled";
    public static final String STATUS_FAILED = "failed";

    public boolean isTerminal() {
        return STATUS_UNINSTALLED.equals(lifecycleStatus) || STATUS_FAILED.equals(lifecycleStatus);
    }
}
