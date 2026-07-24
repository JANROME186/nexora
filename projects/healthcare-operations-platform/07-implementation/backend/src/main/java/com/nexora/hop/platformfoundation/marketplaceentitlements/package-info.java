/**
 * Marketplace Entitlements bounded context, compiled from COM-MOD-017 BCM-PLT-011 Product
 * Marketplace and Entitlements. Hosts six capability sub-packages as sibling bounded contexts
 * inside a single Spring Modulith module, mirroring how integrationinteroperability hosts
 * BCM-PLT-004/BCM-PLT-005: {@code packagecatalog} (AGG-030 MarketplacePackage/PackageVersion),
 * {@code commercialoffers} (AGG-031 CommercialOffer), {@code tenantentitlements} (AGG-032
 * TenantEntitlement, including the centralized {@code EntitlementPolicyEvaluator} required by
 * RN-MKT-005), {@code packageinstallation} (AGG-033 PackageInstallation),
 * {@code compatibilityevaluation} (stateless package/platform compatibility decision) and
 * {@code billingadapter} (the provider-agnostic billing event boundary, mirroring
 * FiscalAdapterPort/IntegrationAdapterPort/NotificationProviderPort/DocumentStoragePort).
 *
 * <p>Per generation-plan.yaml, this backlog item (COM-MOD-017-BE-001) compiles the generatable
 * outputs only: aggregate skeletons, REST controllers rendered from openapi-source.yaml,
 * repository ports/adapters and audit event mappings, plus a basic implementation of each named
 * custom_implementation_point (entitlement policy evaluator, billing adapter boundary,
 * compatibility evaluation strategy, installation rollback orchestration) sufficient for every
 * endpoint to be functional with no 501 response. Deeper policy sophistication (the full
 * evaluation_order chain in entitlement-policy.yaml, IAM/feature-flag/clinical-safety/usage-limit
 * gates, a real billing provider adapter, richer compatibility dimensions beyond platform_version)
 * remains explicit deferred scope for a future COM-MOD-017-BE-002, tracked as TD-BE-018.</p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Marketplace Entitlements",
        allowedDependencies = {"sharedkernel", "organizationmanagement", "auditcompliance"})
package com.nexora.hop.platformfoundation.marketplaceentitlements;
