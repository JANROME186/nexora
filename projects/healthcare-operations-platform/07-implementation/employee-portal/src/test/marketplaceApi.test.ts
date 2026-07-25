import { afterEach, describe, expect, it, vi } from "vitest";
import {
  acceptOffer,
  activateInstallation,
  certifyPackageVersion,
  getPackage,
  getPackageVersion,
  grantEntitlement,
  installPackage,
  listInstallations,
  listOffers,
  listPublishedPackages,
  listTenantEntitlements,
  publishOffer,
  publishPackage,
  retirePackageVersion,
  revokeEntitlement,
  rollbackInstallation,
  submitPackage,
  suspendInstallation,
  uninstallInstallation,
  upgradeInstallation,
} from "../api/marketplaceApi";

function mockFetchOnce(response: Partial<Response> & { jsonBody?: unknown }) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok: response.ok ?? true,
    status: response.status ?? 200,
    statusText: response.statusText ?? "OK",
    json: async () => response.jsonBody,
  } as Response);
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

describe("marketplaceApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    window.localStorage.clear();
  });

  // PackageCatalogController
  it("lists published packages via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ packageId: "pkg-1" }] });
    const result = await listPublishedPackages();
    expect(result).toEqual([{ packageId: "pkg-1" }]);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/packages",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("submits a package via POST", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { packageId: "pkg-2" } });
    await submitPackage({
      code: "LAB-CORE",
      name: "Lab Core",
      category: "clinical",
      capabilityMappings: ["BCM-LAB-001"],
      initialVersion: "1.0.0",
      actorId: "admin-1",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/packages",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("gets a package via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { packageId: "pkg-1" } });
    await getPackage("pkg-1");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/packages/pkg-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("publishes a package version via POST", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { packageId: "pkg-1" } });
    await publishPackage("pkg-1", { version: "1.0.0", actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/packages/pkg-1/publish",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("gets a package version via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { versionId: "ver-1" } });
    await getPackageVersion("pkg-1", "1.0.0");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/packages/pkg-1/versions/1.0.0",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("certifies a package version via POST", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { versionId: "ver-1" } });
    await certifyPackageVersion("pkg-1", "1.0.0", {
      compatibilityApproved: true,
      securityReviewApproved: true,
      supportModelApproved: true,
      telemetryModelApproved: true,
      actorId: "qa-1",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/packages/pkg-1/versions/1.0.0/certify",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("retires a package version via POST", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { versionId: "ver-1", lifecycleStatus: "retired" },
    });
    await retirePackageVersion("pkg-1", "1.0.0", { actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/packages/pkg-1/versions/1.0.0/retire",
      expect.objectContaining({ method: "POST" }),
    );
  });

  // CommercialOfferController
  it("lists offers via GET without a filter", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ offerId: "offer-1" }] });
    await listOffers();
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/offers",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("lists offers via GET filtered by packageId", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ offerId: "offer-1" }] });
    await listOffers("pkg-1");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/offers?packageId=pkg-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("publishes an offer via POST", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { offerId: "offer-2" } });
    await publishOffer({
      packageId: "pkg-1",
      packageVersion: "1.0.0",
      offerCode: "STANDARD",
      offerType: "subscription",
      actorId: "admin-1",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/offers",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("accepts an offer via POST", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { offerId: "offer-1", entitlementId: "ent-1" } });
    await acceptOffer("offer-1", { tenantId: "tenant-1", actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/offers/offer-1/accept",
      expect.objectContaining({ method: "POST" }),
    );
  });

  // TenantEntitlementController
  it("lists tenant entitlements via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ entitlementId: "ent-1" }] });
    await listTenantEntitlements("tenant-1");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/entitlements/tenant-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("grants an entitlement via POST", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { entitlementId: "ent-2" } });
    await grantEntitlement("tenant-1", { packageId: "pkg-1", actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/entitlements/tenant-1",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("revokes an entitlement via POST", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { entitlementId: "ent-1", status: "revoked" } });
    await revokeEntitlement("tenant-1", "ent-1", { reason: "downgrade", actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/entitlements/tenant-1/ent-1/revoke",
      expect.objectContaining({ method: "POST" }),
    );
  });

  // PackageInstallationController
  it("lists installations via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ installationId: "inst-1" }] });
    await listInstallations("tenant-1");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/installations/tenant-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("installs a package via POST", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { installationId: "inst-2" } });
    await installPackage("tenant-1", { packageId: "pkg-1", version: "1.0.0", actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/installations/tenant-1",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("activates an installation via POST", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { installationId: "inst-1", lifecycleStatus: "active" },
    });
    await activateInstallation("tenant-1", "inst-1", { actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/installations/tenant-1/inst-1/activate",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("suspends an installation via POST", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { installationId: "inst-1", lifecycleStatus: "suspended" },
    });
    await suspendInstallation("tenant-1", "inst-1", { actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/installations/tenant-1/inst-1/suspend",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("uninstalls an installation via POST", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { installationId: "inst-1", lifecycleStatus: "uninstalled" },
    });
    await uninstallInstallation("tenant-1", "inst-1", { actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/installations/tenant-1/inst-1/uninstall",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("upgrades an installation via POST", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { installationId: "inst-1", version: "1.1.0" } });
    await upgradeInstallation("tenant-1", "inst-1", { targetVersion: "1.1.0", actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/installations/tenant-1/inst-1/upgrade",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("rolls back an installation via POST", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { installationId: "inst-1", version: "1.0.0" } });
    await rollbackInstallation("tenant-1", "inst-1", { actorId: "admin-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/marketplace/installations/tenant-1/inst-1/upgrade/rollback",
      expect.objectContaining({ method: "POST" }),
    );
  });
});
