import { get } from "./httpClient";
import type { PublicMarketplaceOfferSnapshot, PublicMarketplacePackageSnapshot } from "./types";

const BASE = "/api/public/marketplace";

export function listPublishedMarketplacePackages(): Promise<PublicMarketplacePackageSnapshot[]> {
  return get(`${BASE}/packages/published`);
}

export function getPublishedMarketplacePackageSnapshot(
  packageId: string,
): Promise<PublicMarketplacePackageSnapshot> {
  return get(`${BASE}/packages/${encodeURIComponent(packageId)}/published-snapshot`);
}

export function listPublishedMarketplaceOffers(
  packageId?: string,
): Promise<PublicMarketplaceOfferSnapshot[]> {
  const query = packageId ? `?packageId=${encodeURIComponent(packageId)}` : "";
  return get(`${BASE}/offers/published${query}`);
}
