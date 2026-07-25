/**
 * Product Marketplace and Entitlements API client (COM-MOD-017-FE-001).
 *
 * Covers employee-portal administration endpoints generated from BCM-PLT-011:
 *   PackageCatalogController      /api/marketplace/packages
 *   CommercialOfferController     /api/marketplace/offers
 *   TenantEntitlementController   /api/marketplace/entitlements/{tenantId}
 *   PackageInstallationController /api/marketplace/installations/{tenantId}
 *
 * Mirrors the `externalQualityComplianceApi.ts` shape (TD-STACK-003): a thin, typed operation
 * facade over the hand-written HTTP adapter, one function per backend endpoint. Backend
 * marketplace logic (entitlement policy evaluation, compatibility evaluation, installation
 * lifecycle) is never reimplemented here. Packages and offers are a global catalog (no tenantId
 * path/query segment); entitlements and installations are tenant-scoped ({tenantId} path segment).
 */
import { get, post } from "./httpClient";
import type {
  AcceptOfferRequest,
  AcceptOfferResult,
  CertifyVersionRequest,
  CommercialOffer,
  GrantEntitlementRequest,
  InstallPackageRequest,
  MarketplaceActorRequest,
  MarketplacePackageRecord,
  PackageInstallation,
  PackageVersionRecord,
  PublishOfferRequest,
  PublishPackageRequest,
  RevokeEntitlementRequest,
  SubmitPackageRequest,
  TenantEntitlement,
  UpgradePackageRequest,
} from "./types";

const PACKAGES_BASE = "/api/marketplace/packages";
const OFFERS_BASE = "/api/marketplace/offers";
const ENTITLEMENTS_BASE = "/api/marketplace/entitlements";
const INSTALLATIONS_BASE = "/api/marketplace/installations";

function encode(value: string): string {
  return encodeURIComponent(value);
}

// -- PackageCatalogController --------------------------------------------------------------

export function listPublishedPackages(): Promise<MarketplacePackageRecord[]> {
  return get<MarketplacePackageRecord[]>(PACKAGES_BASE);
}

export function submitPackage(request: SubmitPackageRequest): Promise<MarketplacePackageRecord> {
  return post<MarketplacePackageRecord>(PACKAGES_BASE, request);
}

export function getPackage(packageId: string): Promise<MarketplacePackageRecord> {
  return get<MarketplacePackageRecord>(`${PACKAGES_BASE}/${encode(packageId)}`);
}

export function publishPackage(
  packageId: string,
  request: PublishPackageRequest,
): Promise<MarketplacePackageRecord> {
  return post<MarketplacePackageRecord>(`${PACKAGES_BASE}/${encode(packageId)}/publish`, request);
}

export function getPackageVersion(
  packageId: string,
  version: string,
): Promise<PackageVersionRecord> {
  return get<PackageVersionRecord>(
    `${PACKAGES_BASE}/${encode(packageId)}/versions/${encode(version)}`,
  );
}

export function certifyPackageVersion(
  packageId: string,
  version: string,
  request: CertifyVersionRequest,
): Promise<PackageVersionRecord> {
  return post<PackageVersionRecord>(
    `${PACKAGES_BASE}/${encode(packageId)}/versions/${encode(version)}/certify`,
    request,
  );
}

export function retirePackageVersion(
  packageId: string,
  version: string,
  request: MarketplaceActorRequest,
): Promise<PackageVersionRecord> {
  return post<PackageVersionRecord>(
    `${PACKAGES_BASE}/${encode(packageId)}/versions/${encode(version)}/retire`,
    request,
  );
}

// -- CommercialOfferController -------------------------------------------------------------

export function listOffers(packageId?: string): Promise<CommercialOffer[]> {
  const query = packageId ? `?packageId=${encode(packageId)}` : "";
  return get<CommercialOffer[]>(`${OFFERS_BASE}${query}`);
}

export function publishOffer(request: PublishOfferRequest): Promise<CommercialOffer> {
  return post<CommercialOffer>(OFFERS_BASE, request);
}

export function acceptOffer(
  offerId: string,
  request: AcceptOfferRequest,
): Promise<AcceptOfferResult> {
  return post<AcceptOfferResult>(`${OFFERS_BASE}/${encode(offerId)}/accept`, request);
}

// -- TenantEntitlementController -----------------------------------------------------------

export function listTenantEntitlements(tenantId: string): Promise<TenantEntitlement[]> {
  return get<TenantEntitlement[]>(`${ENTITLEMENTS_BASE}/${encode(tenantId)}`);
}

export function grantEntitlement(
  tenantId: string,
  request: GrantEntitlementRequest,
): Promise<TenantEntitlement> {
  return post<TenantEntitlement>(`${ENTITLEMENTS_BASE}/${encode(tenantId)}`, request);
}

export function revokeEntitlement(
  tenantId: string,
  entitlementId: string,
  request: RevokeEntitlementRequest,
): Promise<TenantEntitlement> {
  return post<TenantEntitlement>(
    `${ENTITLEMENTS_BASE}/${encode(tenantId)}/${encode(entitlementId)}/revoke`,
    request,
  );
}

// -- PackageInstallationController ---------------------------------------------------------

export function listInstallations(tenantId: string): Promise<PackageInstallation[]> {
  return get<PackageInstallation[]>(`${INSTALLATIONS_BASE}/${encode(tenantId)}`);
}

export function installPackage(
  tenantId: string,
  request: InstallPackageRequest,
): Promise<PackageInstallation> {
  return post<PackageInstallation>(`${INSTALLATIONS_BASE}/${encode(tenantId)}`, request);
}

export function activateInstallation(
  tenantId: string,
  installationId: string,
  request: MarketplaceActorRequest,
): Promise<PackageInstallation> {
  return post<PackageInstallation>(
    `${INSTALLATIONS_BASE}/${encode(tenantId)}/${encode(installationId)}/activate`,
    request,
  );
}

export function suspendInstallation(
  tenantId: string,
  installationId: string,
  request: MarketplaceActorRequest,
): Promise<PackageInstallation> {
  return post<PackageInstallation>(
    `${INSTALLATIONS_BASE}/${encode(tenantId)}/${encode(installationId)}/suspend`,
    request,
  );
}

export function uninstallInstallation(
  tenantId: string,
  installationId: string,
  request: MarketplaceActorRequest,
): Promise<PackageInstallation> {
  return post<PackageInstallation>(
    `${INSTALLATIONS_BASE}/${encode(tenantId)}/${encode(installationId)}/uninstall`,
    request,
  );
}

export function upgradeInstallation(
  tenantId: string,
  installationId: string,
  request: UpgradePackageRequest,
): Promise<PackageInstallation> {
  return post<PackageInstallation>(
    `${INSTALLATIONS_BASE}/${encode(tenantId)}/${encode(installationId)}/upgrade`,
    request,
  );
}

export function rollbackInstallation(
  tenantId: string,
  installationId: string,
  request: MarketplaceActorRequest,
): Promise<PackageInstallation> {
  return post<PackageInstallation>(
    `${INSTALLATIONS_BASE}/${encode(tenantId)}/${encode(installationId)}/upgrade/rollback`,
    request,
  );
}
