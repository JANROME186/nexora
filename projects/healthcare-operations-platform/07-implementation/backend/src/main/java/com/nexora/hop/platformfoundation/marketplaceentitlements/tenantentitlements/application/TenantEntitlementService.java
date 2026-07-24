package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.InvalidMarketplaceCommandException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceConflictException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceEntityNotFoundException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlementRepository;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Compiles the generatable outputs of BCM-PLT-011's tenantentitlements capability (AGG-032
 * TenantEntitlement): grant, list and revoke (RN-MKT-002, entitlement-policy.yaml
 * {@code runtime_guards}).
 */
@Service
public class TenantEntitlementService {

    private final TenantEntitlementRepository entitlementRepository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public TenantEntitlementService(
            TenantEntitlementRepository entitlementRepository, TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(entitlementRepository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    TenantEntitlementService(
            TenantEntitlementRepository entitlementRepository, TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder, Clock clock) {
        this.entitlementRepository = entitlementRepository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public TenantEntitlement grantEntitlement(
            String tenantId, String packageId, String offerId, LocalDateTime expiresAt, String actorId) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        if (!tenantDirectory.tenantExists(tenant)) {
            throw new MarketplaceEntityNotFoundException("Tenant was not found.", MarketplaceErrorCodes.TENANT_NOT_FOUND);
        }
        String pkg = requiredText(packageId, "Package id is required.");
        String actor = requiredText(actorId, "Actor id is required.");

        LocalDateTime now = LocalDateTime.now(clock);
        AuditMetadata audit = new AuditMetadata(actor, now, actor, now);
        TenantEntitlement granted = entitlementRepository.save(new TenantEntitlement(
                newId(), tenant, pkg, offerId, TenantEntitlement.STATUS_ACTIVE, now, expiresAt, null, audit));
        auditRecorder.recordSystemEvent(tenant, "TenantEntitlementGranted", "TenantEntitlement",
                granted.entitlementId(), "{\"packageId\":\"%s\"}".formatted(pkg));
        return granted;
    }

    public List<TenantEntitlement> listTenantEntitlements(String tenantId) {
        return entitlementRepository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    public TenantEntitlement revokeEntitlement(String tenantId, String entitlementId, String reason, String actorId) {
        TenantEntitlement current = requireOwnedEntitlement(tenantId, entitlementId);
        if (TenantEntitlement.STATUS_REVOKED.equals(current.status())) {
            throw new MarketplaceConflictException(
                    "Entitlement " + entitlementId + " is already revoked.",
                    MarketplaceErrorCodes.ENTITLEMENT_NOT_FOUND);
        }
        TenantEntitlement revoked = entitlementRepository.save(new TenantEntitlement(
                current.entitlementId(), current.tenantId(), current.packageId(), current.offerId(),
                TenantEntitlement.STATUS_REVOKED, current.grantedAt(), current.expiresAt(),
                requiredText(reason, "Revocation reason is required."), touched(current.audit(), actorId)));
        auditRecorder.recordSystemEvent(current.tenantId(), "TenantEntitlementRevoked", "TenantEntitlement",
                revoked.entitlementId(), "{\"reason\":\"%s\"}".formatted(revoked.revokedReason()));
        return revoked;
    }

    private TenantEntitlement requireOwnedEntitlement(String tenantId, String entitlementId) {
        return entitlementRepository.findById(requiredText(entitlementId, "Entitlement id is required."))
                .filter(candidate -> candidate.tenantId().equals(requiredText(tenantId, "Tenant id is required.")))
                .orElseThrow(() -> new MarketplaceEntityNotFoundException(
                        "Tenant entitlement was not found.", MarketplaceErrorCodes.ENTITLEMENT_NOT_FOUND));
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
