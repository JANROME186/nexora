package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application.CompatibilityEvaluator;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityDecision;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.InstallationStep;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.InstallationStepRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallation;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallationRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.InvalidMarketplaceCommandException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceConflictException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceEntityNotFoundException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application.EntitlementPolicyEvaluator;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.EntitlementDecision;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Compiles the generatable outputs of BCM-PLT-011's packageinstallation capability (AGG-033
 * PackageInstallation): install, activate, suspend, uninstall, upgrade and rollback
 * (installation-model.md, RN-MKT-002, INV-MKT-004). The {@code requested}/{@code
 * compatibility_pending}/{@code approved} intermediate states named by installation-model.md
 * are collapsed into a single deterministic {@link #installPackage} command for BE-001 (no
 * separate approve/compatibility-check endpoint is exposed by openapi-source.md).
 *
 * <p>COM-MOD-017-BE-002 (closing TD-BE-018) appends an {@link InstallationStep} audit-trail
 * record on every lifecycle transition; {@link #rollbackPackage} now derives its target version
 * from that trail (the most recent {@code upgrade} step's {@code fromVersion}) rather than trusting
 * only the single {@code rollbackCheckpointVersion} field, falling back to that field only when the
 * trail has no matching upgrade step recorded.
 */
@Service
public class PackageInstallationService {

    private final PackageInstallationRepository installationRepository;
    private final InstallationStepRepository installationStepRepository;
    private final EntitlementPolicyEvaluator entitlementPolicyEvaluator;
    private final CompatibilityEvaluator compatibilityEvaluator;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PackageInstallationService(
            PackageInstallationRepository installationRepository, InstallationStepRepository installationStepRepository,
            EntitlementPolicyEvaluator entitlementPolicyEvaluator, CompatibilityEvaluator compatibilityEvaluator,
            TenantDirectory tenantDirectory, AuditRecorder auditRecorder) {
        this(installationRepository, installationStepRepository, entitlementPolicyEvaluator, compatibilityEvaluator,
                tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    PackageInstallationService(
            PackageInstallationRepository installationRepository, InstallationStepRepository installationStepRepository,
            EntitlementPolicyEvaluator entitlementPolicyEvaluator, CompatibilityEvaluator compatibilityEvaluator,
            TenantDirectory tenantDirectory, AuditRecorder auditRecorder, Clock clock) {
        this.installationRepository = installationRepository;
        this.installationStepRepository = installationStepRepository;
        this.entitlementPolicyEvaluator = entitlementPolicyEvaluator;
        this.compatibilityEvaluator = compatibilityEvaluator;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** Installation-model.yaml invariant: requires active tenant, compatible package and valid entitlement. */
    public PackageInstallation installPackage(
            String tenantId, String packageId, String version, String entitlementId, String actorId) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        if (!tenantDirectory.tenantExists(tenant)) {
            throw new MarketplaceEntityNotFoundException("Tenant was not found.", MarketplaceErrorCodes.TENANT_NOT_FOUND);
        }
        String pkg = requiredText(packageId, "Package id is required.");
        String requestedVersion = requiredText(version, "Version is required.");
        String actor = requiredText(actorId, "Actor id is required.");

        requireEntitlement(tenant, pkg);
        CompatibilityDecision decision = compatibilityEvaluator.evaluate(pkg, requestedVersion);
        if (!decision.allowsInstallation()) {
            throw new MarketplaceConflictException(
                    "Package " + pkg + " version " + requestedVersion + " failed compatibility evaluation: "
                            + decision.decision() + ".", MarketplaceErrorCodes.COMPATIBILITY_FAILED);
        }
        boolean alreadyInstalled = installationRepository.findByTenantIdAndPackageId(tenant, pkg).stream()
                .anyMatch(candidate -> !candidate.isTerminal());
        if (alreadyInstalled) {
            throw new MarketplaceConflictException(
                    "Tenant " + tenant + " already has a non-terminal installation of package " + pkg + ".",
                    MarketplaceErrorCodes.INSTALLATION_CONFLICT);
        }

        LocalDateTime now = LocalDateTime.now(clock);
        PackageInstallation installed = installationRepository.save(new PackageInstallation(
                newId(), tenant, pkg, entitlementId, requestedVersion, PackageInstallation.STATUS_INSTALLED, null,
                new AuditMetadata(actor, now, actor, now)));
        recordStep(installed, InstallationStep.TYPE_INSTALL, null, requestedVersion, null,
                PackageInstallation.STATUS_INSTALLED, actor, now);
        auditRecorder.recordSystemEvent(tenant, "PackageInstalled", "PackageInstallation",
                installed.installationId(), "{\"packageId\":\"%s\",\"version\":\"%s\"}".formatted(pkg, requestedVersion));
        return installed;
    }

    public List<PackageInstallation> listInstallations(String tenantId) {
        return installationRepository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    /** RN-MKT-002: activation requires an active entitlement; INV-MKT-004: preserves a rollback checkpoint first. */
    public PackageInstallation activatePackage(String tenantId, String installationId, String actorId) {
        PackageInstallation current = requireOwnedInstallation(tenantId, installationId);
        if (!PackageInstallation.STATUS_INSTALLED.equals(current.lifecycleStatus())
                && !PackageInstallation.STATUS_SUSPENDED.equals(current.lifecycleStatus())) {
            throw new MarketplaceConflictException(
                    "Installation " + installationId + " in status " + current.lifecycleStatus()
                            + " cannot be activated.", MarketplaceErrorCodes.INSTALLATION_CONFLICT);
        }
        requireEntitlement(current.tenantId(), current.packageId());
        String checkpoint = current.rollbackCheckpointVersion() != null
                ? current.rollbackCheckpointVersion()
                : current.version();
        LocalDateTime now = LocalDateTime.now(clock);
        PackageInstallation activated = installationRepository.save(new PackageInstallation(
                current.installationId(), current.tenantId(), current.packageId(), current.entitlementId(),
                current.version(), PackageInstallation.STATUS_ACTIVE, checkpoint, touched(current.audit(), actorId)));
        recordStep(activated, InstallationStep.TYPE_ACTIVATE, current.version(), current.version(),
                current.lifecycleStatus(), PackageInstallation.STATUS_ACTIVE, actorId, now);
        auditRecorder.recordSystemEvent(current.tenantId(), "PackageActivated", "PackageInstallation",
                activated.installationId(), "{}");
        return activated;
    }

    public PackageInstallation suspendPackage(String tenantId, String installationId, String actorId) {
        PackageInstallation current = requireOwnedInstallation(tenantId, installationId);
        if (!PackageInstallation.STATUS_ACTIVE.equals(current.lifecycleStatus())) {
            throw new MarketplaceConflictException(
                    "Installation " + installationId + " in status " + current.lifecycleStatus()
                            + " cannot be suspended.", MarketplaceErrorCodes.INSTALLATION_CONFLICT);
        }
        PackageInstallation suspended = installationRepository.save(withStatus(current, PackageInstallation.STATUS_SUSPENDED, actorId));
        recordStep(suspended, InstallationStep.TYPE_SUSPEND, current.version(), current.version(),
                current.lifecycleStatus(), PackageInstallation.STATUS_SUSPENDED, actorId, LocalDateTime.now(clock));
        auditRecorder.recordSystemEvent(current.tenantId(), "PackageSuspended", "PackageInstallation",
                suspended.installationId(), "{}");
        return suspended;
    }

    /** installation-model.md invariant: uninstall preserves audit, billing and entitlement history (soft-disable). */
    public PackageInstallation uninstallPackage(String tenantId, String installationId, String actorId) {
        PackageInstallation current = requireOwnedInstallation(tenantId, installationId);
        if (current.isTerminal()) {
            throw new MarketplaceConflictException(
                    "Installation " + installationId + " is already " + current.lifecycleStatus() + ".",
                    MarketplaceErrorCodes.INSTALLATION_CONFLICT);
        }
        PackageInstallation uninstalled = installationRepository.save(withStatus(current, PackageInstallation.STATUS_UNINSTALLED, actorId));
        recordStep(uninstalled, InstallationStep.TYPE_UNINSTALL, current.version(), current.version(),
                current.lifecycleStatus(), PackageInstallation.STATUS_UNINSTALLED, actorId, LocalDateTime.now(clock));
        auditRecorder.recordSystemEvent(current.tenantId(), "PackageUninstalled", "PackageInstallation",
                uninstalled.installationId(), "{}");
        return uninstalled;
    }

    /** Preserves the pre-upgrade version as the rollback checkpoint before applying the new version (INV-MKT-004). */
    public PackageInstallation upgradePackage(String tenantId, String installationId, String targetVersion, String actorId) {
        PackageInstallation current = requireOwnedInstallation(tenantId, installationId);
        if (!PackageInstallation.STATUS_ACTIVE.equals(current.lifecycleStatus())) {
            throw new MarketplaceConflictException(
                    "Installation " + installationId + " in status " + current.lifecycleStatus()
                            + " cannot be upgraded.", MarketplaceErrorCodes.INSTALLATION_CONFLICT);
        }
        String upgradeTargetVersion = requiredText(targetVersion, "Target version is required.");
        CompatibilityDecision decision = compatibilityEvaluator.evaluate(current.packageId(), upgradeTargetVersion);
        if (!decision.allowsInstallation()) {
            throw new MarketplaceConflictException(
                    "Target version " + upgradeTargetVersion + " failed compatibility evaluation: "
                            + decision.decision() + ".", MarketplaceErrorCodes.COMPATIBILITY_FAILED);
        }
        PackageInstallation upgraded = installationRepository.save(new PackageInstallation(
                current.installationId(), current.tenantId(), current.packageId(), current.entitlementId(),
                upgradeTargetVersion, PackageInstallation.STATUS_ACTIVE, current.version(),
                touched(current.audit(), actorId)));
        recordStep(upgraded, InstallationStep.TYPE_UPGRADE, current.version(), upgradeTargetVersion,
                PackageInstallation.STATUS_ACTIVE, PackageInstallation.STATUS_ACTIVE, actorId, LocalDateTime.now(clock));
        auditRecorder.recordSystemEvent(current.tenantId(), "PackageUpgraded", "PackageInstallation",
                upgraded.installationId(), "{\"targetVersion\":\"%s\"}".formatted(upgradeTargetVersion));
        return upgraded;
    }

    /**
     * Derives the rollback target from the installation's {@link InstallationStep} audit trail
     * (the most recent {@code upgrade} step that produced the current version), falling back to
     * {@code rollbackCheckpointVersion} only when the trail has no matching upgrade step.
     */
    public PackageInstallation rollbackPackage(String tenantId, String installationId, String actorId) {
        PackageInstallation current = requireOwnedInstallation(tenantId, installationId);
        String rollbackTarget = resolveRollbackTarget(current);
        if (rollbackTarget == null) {
            throw new MarketplaceConflictException(
                    "Installation " + installationId + " has no rollback checkpoint available.",
                    MarketplaceErrorCodes.ROLLBACK_NOT_AVAILABLE);
        }
        LocalDateTime now = LocalDateTime.now(clock);
        PackageInstallation rolledBack = installationRepository.save(new PackageInstallation(
                current.installationId(), current.tenantId(), current.packageId(), current.entitlementId(),
                rollbackTarget, PackageInstallation.STATUS_ACTIVE, rollbackTarget, touched(current.audit(), actorId)));
        recordStep(rolledBack, InstallationStep.TYPE_ROLLBACK, current.version(), rollbackTarget,
                current.lifecycleStatus(), PackageInstallation.STATUS_ACTIVE, actorId, now);
        auditRecorder.recordSystemEvent(current.tenantId(), "PackageRolledBack", "PackageInstallation",
                rolledBack.installationId(), "{}");
        return rolledBack;
    }

    private String resolveRollbackTarget(PackageInstallation current) {
        List<InstallationStep> steps = installationStepRepository.findByInstallationIdOrderByOccurredAt(current.installationId());
        Optional<InstallationStep> lastMatchingUpgrade = steps.stream()
                .filter(step -> InstallationStep.TYPE_UPGRADE.equals(step.stepType())
                        && current.version().equals(step.toVersion()))
                .reduce((first, second) -> second);
        return lastMatchingUpgrade.map(InstallationStep::fromVersion).orElse(current.rollbackCheckpointVersion());
    }

    private void requireEntitlement(String tenantId, String packageId) {
        EntitlementDecision decision = entitlementPolicyEvaluator.evaluate(tenantId, packageId);
        if (!decision.allowed()) {
            throw new MarketplaceConflictException(decision.reason(), errorCodeFor(decision.decision()));
        }
    }

    /** Maps every entitlement-policy.md {@code decision_outputs} value to its marketplace error code. */
    private static String errorCodeFor(String decision) {
        return switch (decision) {
            case EntitlementDecision.DENIED_EXPIRED -> MarketplaceErrorCodes.ENTITLEMENT_EXPIRED;
            case EntitlementDecision.DENIED_TENANT_INACTIVE -> MarketplaceErrorCodes.TENANT_NOT_ACTIVE;
            case EntitlementDecision.DENIED_SUSPENDED_PACKAGE -> MarketplaceErrorCodes.PACKAGE_SUSPENDED;
            case EntitlementDecision.DENIED_INCOMPATIBLE_VERSION -> MarketplaceErrorCodes.COMPATIBILITY_FAILED;
            case EntitlementDecision.DENIED_MISSING_PERMISSION -> MarketplaceErrorCodes.PERMISSION_REQUIRED;
            case EntitlementDecision.DENIED_FEATURE_DISABLED -> MarketplaceErrorCodes.FEATURE_NOT_ENABLED;
            case EntitlementDecision.DENIED_CLINICAL_SAFETY_ACKNOWLEDGEMENT_REQUIRED ->
                    MarketplaceErrorCodes.CLINICAL_SAFETY_ACKNOWLEDGEMENT_REQUIRED;
            case EntitlementDecision.DENIED_USAGE_LIMIT -> MarketplaceErrorCodes.USAGE_LIMIT_EXCEEDED;
            default -> MarketplaceErrorCodes.ENTITLEMENT_REQUIRED;
        };
    }

    private PackageInstallation requireOwnedInstallation(String tenantId, String installationId) {
        return installationRepository.findById(requiredText(installationId, "Installation id is required."))
                .filter(candidate -> candidate.tenantId().equals(requiredText(tenantId, "Tenant id is required.")))
                .orElseThrow(() -> new MarketplaceEntityNotFoundException(
                        "Package installation was not found.", MarketplaceErrorCodes.INSTALLATION_NOT_FOUND));
    }

    private PackageInstallation withStatus(PackageInstallation source, String status, String actorId) {
        return new PackageInstallation(
                source.installationId(), source.tenantId(), source.packageId(), source.entitlementId(),
                source.version(), status, source.rollbackCheckpointVersion(), touched(source.audit(), actorId));
    }

    private void recordStep(
            PackageInstallation installation, String stepType, String fromVersion, String toVersion,
            String fromStatus, String toStatus, String actorId, LocalDateTime occurredAt) {
        installationStepRepository.save(new InstallationStep(
                UUID.randomUUID().toString(), installation.installationId(), installation.tenantId(), stepType,
                fromVersion, toVersion, fromStatus, toStatus, actorId, occurredAt));
    }

    private AuditMetadata touched(AuditMetadata audit, String actorId) {
        return new AuditMetadata(
                audit.createdBy(), audit.createdAt(), requiredText(actorId, "Actor id is required."),
                LocalDateTime.now(clock));
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidMarketplaceCommandException(message, MarketplaceErrorCodes.MARKETPLACE_COMMAND_INVALID);
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
