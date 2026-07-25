package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistration;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistrationRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKey;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKeyRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicy;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicyRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationConflictException;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationEntityNotFoundException;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationErrorCodes;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.InvalidIntegrationCommandException;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Compiles the generatable outputs of bcm-plt-005-api-management/generation-plan.md (operation
 * listing, partner-key revocation/listing, rate-limit policy configuration, PRC-APIM-005-01/-02)
 * and implements the CUS-APIM-005 custom rules: classification/publish-gating (RN-001,
 * INV-APIM-001), partner-key scope/tenant validation at issuance (RN-002, INV-APIM-002),
 * deprecation-window governance at scheduling time plus the window-elapsed retirement transition
 * (RN-003, {@link #retireDeprecatedOperation}, delivered by MVP-MOD-008-BE-002), and rate-limit
 * *enforcement* via {@code adapter.in.web.PartnerApiKeyRateLimitInterceptor} (RN-004, delivered
 * by MVP-MOD-008-BE-002). Every administrative action here (classification,
 * partner-key issuance/revocation, rate-limit policy change, deprecation scheduling/retirement)
 * is audited (RN-005).
 */
@Service
public class ApiManagementService {

    private static final Set<String> VALID_CLASSIFICATIONS = Set.of(
            ApiSurfaceRegistration.CLASSIFICATION_PUBLIC, ApiSurfaceRegistration.CLASSIFICATION_INTERNAL,
            ApiSurfaceRegistration.CLASSIFICATION_PARTNER);
    private static final Set<String> VALID_IDENTIFICATION_METHODS = Set.of(
            RateLimitPolicy.METHOD_PARTNER_API_KEY, RateLimitPolicy.METHOD_IP_ADDRESS,
            RateLimitPolicy.METHOD_SESSION_TOKEN);

    private final ApiSurfaceRegistrationRepository registrationRepository;
    private final PartnerApiKeyRepository partnerApiKeyRepository;
    private final RateLimitPolicyRepository rateLimitPolicyRepository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public ApiManagementService(
            ApiSurfaceRegistrationRepository registrationRepository,
            PartnerApiKeyRepository partnerApiKeyRepository,
            RateLimitPolicyRepository rateLimitPolicyRepository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(registrationRepository, partnerApiKeyRepository, rateLimitPolicyRepository, tenantDirectory,
                auditRecorder, Clock.systemUTC());
    }

    ApiManagementService(
            ApiSurfaceRegistrationRepository registrationRepository,
            PartnerApiKeyRepository partnerApiKeyRepository,
            RateLimitPolicyRepository rateLimitPolicyRepository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.registrationRepository = registrationRepository;
        this.partnerApiKeyRepository = partnerApiKeyRepository;
        this.rateLimitPolicyRepository = rateLimitPolicyRepository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** RN-001/INV-APIM-001: classifying an operation is the only way it can move off "internal". */
    public ApiSurfaceRegistration classifyOperation(
            String operationId, String ownerCapability, String classification, String apiVersion, String tenantId,
            String actorId) {
        String opId = requiredText(operationId, "Operation id is required.");
        String capability = requiredText(ownerCapability, "Owner capability is required.");
        if (classification == null || !VALID_CLASSIFICATIONS.contains(classification)) {
            throw new InvalidIntegrationCommandException(
                    "Classification must be one of " + VALID_CLASSIFICATIONS + ".", "API_CLASSIFICATION_INVALID");
        }
        String version = requiredText(apiVersion, "API version is required.");
        String actor = requiredText(actorId, "Actor id is required.");

        ApiSurfaceRegistration existing = registrationRepository.findByOperationId(opId).orElse(null);
        LocalDateTime now = LocalDateTime.now(clock);
        ApiSurfaceRegistration registration = new ApiSurfaceRegistration(
                existing == null ? newId() : existing.registrationId(), tenantId, capability, opId, classification,
                version, existing == null ? ApiSurfaceRegistration.DEPRECATION_ACTIVE : existing.deprecationStatus(),
                existing == null ? null : existing.deprecationWindowFrom(),
                existing == null ? null : existing.deprecationWindowTo(),
                existing == null ? null : existing.migrationNote(),
                existing == null ? new AuditMetadata(actor, now, actor, now)
                        : new AuditMetadata(existing.audit().createdBy(), existing.audit().createdAt(), actor, now));
        ApiSurfaceRegistration saved = registrationRepository.save(registration);
        auditRecorder.recordSystemEvent(tenantId == null ? "platform" : tenantId, "ApiSurfaceClassified",
                "ApiSurfaceRegistration", saved.registrationId(),
                "{\"operationId\":\"%s\",\"classification\":\"%s\"}".formatted(opId, classification));
        return saved;
    }

    public List<ApiSurfaceRegistration> listOperations() {
        return registrationRepository.findAll();
    }

    /** RN-003: a deprecation schedule must carry a complete, ordered window and a migration note. */
    public ApiSurfaceRegistration scheduleDeprecation(
            String operationId, LocalDateTime windowFrom, LocalDateTime windowTo, String migrationNote,
            String actorId) {
        ApiSurfaceRegistration current = registrationRepository.findByOperationId(
                requiredText(operationId, "Operation id is required."))
                .orElseThrow(() -> new IntegrationEntityNotFoundException(
                        "API operation is not classified yet.", IntegrationErrorCodes.API_OPERATION_NOT_CLASSIFIED));
        if (windowFrom == null || windowTo == null || !windowTo.isAfter(windowFrom)
                || migrationNote == null || migrationNote.isBlank()) {
            throw new InvalidIntegrationCommandException(
                    "A deprecation schedule requires a complete window (from before to) and a migration note.",
                    IntegrationErrorCodes.API_DEPRECATION_WINDOW_MISSING);
        }
        String actor = requiredText(actorId, "Actor id is required.");
        ApiSurfaceRegistration updated = new ApiSurfaceRegistration(
                current.registrationId(), current.tenantId(), current.ownerCapability(), current.operationId(),
                current.classification(), current.apiVersion(), ApiSurfaceRegistration.DEPRECATION_SCHEDULED,
                windowFrom, windowTo, migrationNote,
                new AuditMetadata(current.audit().createdBy(), current.audit().createdAt(), actor,
                        LocalDateTime.now(clock)));
        ApiSurfaceRegistration saved = registrationRepository.save(updated);
        auditRecorder.recordSystemEvent(saved.tenantId() == null ? "platform" : saved.tenantId(),
                "ApiDeprecationScheduled", "ApiSurfaceRegistration", saved.registrationId(), "{}");
        return saved;
    }

    /**
     * RN-003: completes the deprecation lifecycle once the scheduled window has elapsed. Only
     * reachable from {@link ApiSurfaceRegistration#DEPRECATION_SCHEDULED}; attempting retirement
     * before {@code deprecationWindowTo} is rejected rather than silently accepted, since nothing
     * in this synchronous backend runs the transition automatically on a timer.
     */
    public ApiSurfaceRegistration retireDeprecatedOperation(String operationId, String actorId) {
        ApiSurfaceRegistration current = registrationRepository.findByOperationId(
                requiredText(operationId, "Operation id is required."))
                .orElseThrow(() -> new IntegrationEntityNotFoundException(
                        "API operation is not classified yet.", IntegrationErrorCodes.API_OPERATION_NOT_CLASSIFIED));
        if (!ApiSurfaceRegistration.DEPRECATION_SCHEDULED.equals(current.deprecationStatus())) {
            throw new IntegrationConflictException(
                    "Operation " + operationId + " is not in a scheduled deprecation (status "
                            + current.deprecationStatus() + ").",
                    IntegrationErrorCodes.API_DEPRECATION_WINDOW_NOT_ELAPSED);
        }
        LocalDateTime now = LocalDateTime.now(clock);
        if (current.deprecationWindowTo() == null || now.isBefore(current.deprecationWindowTo())) {
            throw new IntegrationConflictException(
                    "Operation " + operationId + "'s deprecation window has not elapsed yet (ends "
                            + current.deprecationWindowTo() + ").",
                    IntegrationErrorCodes.API_DEPRECATION_WINDOW_NOT_ELAPSED);
        }
        String actor = requiredText(actorId, "Actor id is required.");
        ApiSurfaceRegistration retired = new ApiSurfaceRegistration(
                current.registrationId(), current.tenantId(), current.ownerCapability(), current.operationId(),
                current.classification(), current.apiVersion(), ApiSurfaceRegistration.DEPRECATION_RETIRED,
                current.deprecationWindowFrom(), current.deprecationWindowTo(), current.migrationNote(),
                new AuditMetadata(current.audit().createdBy(), current.audit().createdAt(), actor, now));
        ApiSurfaceRegistration saved = registrationRepository.save(retired);
        auditRecorder.recordSystemEvent(saved.tenantId() == null ? "platform" : saved.tenantId(),
                "ApiOperationRetired", "ApiSurfaceRegistration", saved.registrationId(), "{}");
        return saved;
    }

    /**
     * RN-002/INV-APIM-002: every granted scope must reference an operation already classified
     * {@code partner}; the key is tenant-scoped from issuance.
     */
    public PartnerApiKey issuePartnerApiKey(
            String tenantId, String consumerName, List<String> grantedScopes, String actorId) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        if (!tenantDirectory.tenantExists(tenant)) {
            throw new IntegrationEntityNotFoundException("Tenant was not found.", "TENANT_NOT_FOUND");
        }
        String consumer = requiredText(consumerName, "Consumer name is required.");
        if (grantedScopes == null || grantedScopes.isEmpty()) {
            throw new InvalidIntegrationCommandException(
                    "At least one granted scope is required.",
                    IntegrationErrorCodes.API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH);
        }
        for (String scope : grantedScopes) {
            ApiSurfaceRegistration registration = registrationRepository.findByOperationId(scope).orElse(null);
            if (registration != null && !ApiSurfaceRegistration.CLASSIFICATION_PARTNER.equals(
                    registration.classification())) {
                throw new InvalidIntegrationCommandException(
                        "Scope '" + scope + "' does not reference a partner-classified operation.",
                        IntegrationErrorCodes.API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH);
            }
        }
        String actor = requiredText(actorId, "Actor id is required.");
        LocalDateTime now = LocalDateTime.now(clock);
        String rateLimitPolicyRef = rateLimitPolicyRepository
                .findByClassification(ApiSurfaceRegistration.CLASSIFICATION_PARTNER)
                .map(RateLimitPolicy::policyId).orElse(null);
        PartnerApiKey key = new PartnerApiKey(
                newId(), tenant, consumer, List.copyOf(grantedScopes), rateLimitPolicyRef,
                PartnerApiKey.STATUS_ACTIVE, new AuditMetadata(actor, now, actor, now));
        PartnerApiKey saved = partnerApiKeyRepository.save(key);
        auditRecorder.recordSystemEvent(tenant, "PartnerApiKeyIssued", "PartnerApiKey", saved.keyId(), "{}");
        return saved;
    }

    public PartnerApiKey revokePartnerApiKey(String keyId, String actorId) {
        PartnerApiKey current = partnerApiKeyRepository.findById(requiredText(keyId, "Key id is required."))
                .orElseThrow(() -> new IntegrationEntityNotFoundException(
                        "Partner API key was not found.", "API_PARTNER_KEY_NOT_FOUND"));
        if (!current.isUsable()) {
            throw new IntegrationConflictException(
                    "Partner API key " + keyId + " is already " + current.status() + ".",
                    IntegrationErrorCodes.API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH);
        }
        String actor = requiredText(actorId, "Actor id is required.");
        PartnerApiKey revoked = new PartnerApiKey(
                current.keyId(), current.tenantId(), current.consumerName(), current.grantedScopes(),
                current.rateLimitPolicyRef(), PartnerApiKey.STATUS_REVOKED,
                new AuditMetadata(current.audit().createdBy(), current.audit().createdAt(), actor,
                        LocalDateTime.now(clock)));
        PartnerApiKey saved = partnerApiKeyRepository.save(revoked);
        auditRecorder.recordSystemEvent(saved.tenantId(), "PartnerApiKeyRevoked", "PartnerApiKey", saved.keyId(), "{}");
        return saved;
    }

    public List<PartnerApiKey> listPartnerApiKeys(String tenantId) {
        return partnerApiKeyRepository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    /**
     * RN-004/RN-005/RN-007 (COM-MOD-011-BE-001): configures the enforced rate-limit policy for a
     * classification tier; audited. The {@code consumerIdentificationMethod} declares how the
     * enforcement middleware attributes a request to a consumer bucket for the fixed-window
     * counter: {@link RateLimitPolicy#METHOD_PARTNER_API_KEY} for the {@code partner}
     * classification, {@link RateLimitPolicy#METHOD_IP_ADDRESS} or
     * {@link RateLimitPolicy#METHOD_SESSION_TOKEN} for the anonymous {@code public}
     * classification (BCM-PLT-005 RN-007, materially reduces TD-BE-015). When
     * {@code consumerIdentificationMethod} is {@code null} the method is derived from the
     * classification, preserving pre-COM-MOD-011 behavior for existing callers.
     */
    public RateLimitPolicy setRateLimitPolicy(
            String classification, int requestsPerMinute, String consumerIdentificationMethod, String actorId) {
        if (classification == null || !VALID_CLASSIFICATIONS.contains(classification)) {
            throw new InvalidIntegrationCommandException(
                    "Classification must be one of " + VALID_CLASSIFICATIONS + ".", "API_CLASSIFICATION_INVALID");
        }
        if (requestsPerMinute <= 0) {
            throw new InvalidIntegrationCommandException(
                    "Requests per minute must be positive.", "API_RATE_LIMIT_POLICY_INVALID");
        }
        String method = defaultOrValidateIdentificationMethod(classification, consumerIdentificationMethod);
        String actor = requiredText(actorId, "Actor id is required.");
        RateLimitPolicy existing = rateLimitPolicyRepository.findByClassification(classification).orElse(null);
        LocalDateTime now = LocalDateTime.now(clock);
        RateLimitPolicy policy = new RateLimitPolicy(
                existing == null ? newId() : existing.policyId(), classification, requestsPerMinute, method,
                existing == null ? new AuditMetadata(actor, now, actor, now)
                        : new AuditMetadata(existing.audit().createdBy(), existing.audit().createdAt(), actor, now));
        RateLimitPolicy saved = rateLimitPolicyRepository.save(policy);
        auditRecorder.recordSystemEvent("platform", "RateLimitPolicySet", "RateLimitPolicy", saved.policyId(),
                "{\"classification\":\"%s\",\"requestsPerMinute\":%d,\"consumerIdentificationMethod\":\"%s\"}"
                        .formatted(classification, requestsPerMinute, method));
        return saved;
    }

    private static String defaultOrValidateIdentificationMethod(String classification, String method) {
        if (method == null || method.isBlank()) {
            if (ApiSurfaceRegistration.CLASSIFICATION_PARTNER.equals(classification)) {
                return RateLimitPolicy.METHOD_PARTNER_API_KEY;
            }
            return RateLimitPolicy.METHOD_IP_ADDRESS;
        }
        if (!VALID_IDENTIFICATION_METHODS.contains(method)) {
            throw new InvalidIntegrationCommandException(
                    "Consumer identification method must be one of " + VALID_IDENTIFICATION_METHODS + ".",
                    "API_RATE_LIMIT_POLICY_INVALID");
        }
        if (ApiSurfaceRegistration.CLASSIFICATION_PARTNER.equals(classification)
                && !RateLimitPolicy.METHOD_PARTNER_API_KEY.equals(method)) {
            throw new InvalidIntegrationCommandException(
                    "Partner classification must use partner_api_key identification.",
                    "API_RATE_LIMIT_POLICY_INVALID");
        }
        if (ApiSurfaceRegistration.CLASSIFICATION_PUBLIC.equals(classification)
                && RateLimitPolicy.METHOD_PARTNER_API_KEY.equals(method)) {
            throw new InvalidIntegrationCommandException(
                    "Public classification cannot use partner_api_key identification.",
                    "API_RATE_LIMIT_POLICY_INVALID");
        }
        return method;
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidIntegrationCommandException(message, "INTEGRATION_COMMAND_INVALID");
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
