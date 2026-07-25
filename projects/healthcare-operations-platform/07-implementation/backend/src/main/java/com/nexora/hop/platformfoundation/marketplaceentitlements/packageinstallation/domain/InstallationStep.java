package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain;

import java.time.LocalDateTime;

/**
 * A persisted, append-only lifecycle-transition record for a {@link PackageInstallation}
 * (COM-MOD-017-BE-002, closing TD-BE-018's installation-rollback-orchestration
 * custom_implementation_point). Every install/activate/suspend/uninstall/upgrade/rollback command
 * appends one step, giving rollback a real multi-step audit trail to derive its target version
 * from instead of trusting only the single {@code PackageInstallation.rollbackCheckpointVersion}
 * field.
 */
public record InstallationStep(
        String stepId,
        String installationId,
        String tenantId,
        String stepType,
        String fromVersion,
        String toVersion,
        String fromStatus,
        String toStatus,
        String actorId,
        LocalDateTime occurredAt) {

    public static final String TYPE_INSTALL = "install";
    public static final String TYPE_ACTIVATE = "activate";
    public static final String TYPE_SUSPEND = "suspend";
    public static final String TYPE_UNINSTALL = "uninstall";
    public static final String TYPE_UPGRADE = "upgrade";
    public static final String TYPE_ROLLBACK = "rollback";
}
