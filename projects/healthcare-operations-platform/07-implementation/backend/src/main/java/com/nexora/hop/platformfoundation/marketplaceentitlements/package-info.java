/**
 * Marketplace Entitlements bounded context, compiled from COM-MOD-017 BCM-PLT-011 Product
 * Marketplace and Entitlements. Hosts six capability sub-packages as sibling bounded contexts
 * inside a single Spring Modulith module, mirroring how integrationinteroperability hosts
 * BCM-PLT-004/BCM-PLT-005: {@code packagecatalog} (AGG-030 MarketplacePackage/PackageVersion),
 * {@code commercialoffers} (AGG-031 CommercialOffer), {@code tenantentitlements} (AGG-032
 * TenantEntitlement, including the centralized {@code EntitlementPolicyEvaluator} required by
 * RN-MKT-005), {@code packageinstallation} (AGG-033 PackageInstallation),
 * {@code compatibilityevaluation} (package/platform compatibility decision) and
 * {@code billingadapter} (the provider-agnostic billing event boundary, mirroring
 * FiscalAdapterPort/IntegrationAdapterPort/NotificationProviderPort/DocumentStoragePort).
 *
 * <p>COM-MOD-017-BE-001 compiled the generatable outputs only: aggregate skeletons, REST
 * controllers rendered from openapi-source.md, repository ports/adapters, audit event mappings and
 * a basic implementation of each named custom_implementation_point, sufficient for every endpoint
 * to be functional with no 501 response, and registered TD-BE-018 for deeper policy sophistication.
 * COM-MOD-017-BE-002 closes TD-BE-018: {@code EntitlementPolicyEvaluator} now implements the full
 * entitlement-policy.md {@code evaluation_order} (tenant_status/package_status/license_status/
 * compatibility_status/iam_permission/feature_flag/clinical_safety_control/usage_limit) — the
 * {@code iam_permission}/{@code feature_flag} steps take caller-resolved facts rather than pulling
 * from identityaccess/platformconfiguration directly, keeping this module's Spring Modulith
 * dependency graph unchanged and acyclic; {@code CompatibilityEvaluator} evaluates all 9
 * compatibility.md dimensions the same way; {@code billingadapter} gained retry/idempotency; and
 * {@code packageinstallation} gained a persisted multi-step {@code InstallationStep} audit trail
 * for rollback orchestration.</p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Marketplace Entitlements",
        allowedDependencies = {"sharedkernel", "organizationmanagement", "auditcompliance"})
package com.nexora.hop.platformfoundation.marketplaceentitlements;
