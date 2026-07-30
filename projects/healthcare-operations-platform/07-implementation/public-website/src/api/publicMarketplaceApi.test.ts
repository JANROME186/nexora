import { beforeEach, describe, expect, it, vi } from "vitest";
import * as httpClient from "./httpClient";
import {
  getPublishedMarketplacePackageSnapshot,
  listPublishedMarketplaceOffers,
  listPublishedMarketplacePackages,
} from "./publicMarketplaceApi";

vi.mock("./httpClient");

describe("publicMarketplaceApi", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("calls listPublishedMarketplacePackages", async () => {
    const mockPackages = [
      {
        packageId: "pkg-1",
        code: "P1",
        name: "Package 1",
        category: "clinical",
        capabilityMappings: ["BCM-CLI-001"],
        status: "published",
      },
    ];
    vi.spyOn(httpClient, "get").mockResolvedValue(mockPackages);

    const result = await listPublishedMarketplacePackages();
    expect(httpClient.get).toHaveBeenCalledWith("/api/public/marketplace/packages/published");
    expect(result).toEqual(mockPackages);
  });

  it("calls getPublishedMarketplacePackageSnapshot", async () => {
    const mockPackage = {
      packageId: "pkg-1",
      code: "P1",
      name: "Package 1",
      category: "clinical",
      capabilityMappings: ["BCM-CLI-001"],
      status: "published",
    };
    vi.spyOn(httpClient, "get").mockResolvedValue(mockPackage);

    const result = await getPublishedMarketplacePackageSnapshot("pkg-1");
    expect(httpClient.get).toHaveBeenCalledWith(
      "/api/public/marketplace/packages/pkg-1/published-snapshot",
    );
    expect(result).toEqual(mockPackage);
  });

  it("calls listPublishedMarketplaceOffers without packageId filter", async () => {
    const mockOffers = [
      {
        offerId: "off-1",
        packageId: "pkg-1",
        packageVersion: "1.0.0",
        offerCode: "O1",
        offerType: "base_plan",
        lifecycleStatus: "published",
        tierCodes: ["STANDARD"],
        trialPeriodDays: 30,
        billingEventRulesSummary: "Free trial",
      },
    ];
    vi.spyOn(httpClient, "get").mockResolvedValue(mockOffers);

    const result = await listPublishedMarketplaceOffers();
    expect(httpClient.get).toHaveBeenCalledWith("/api/public/marketplace/offers/published");
    expect(result).toEqual(mockOffers);
  });

  it("calls listPublishedMarketplaceOffers with packageId filter", async () => {
    const mockOffers = [
      {
        offerId: "off-1",
        packageId: "pkg-1",
        packageVersion: "1.0.0",
        offerCode: "O1",
        offerType: "base_plan",
        lifecycleStatus: "published",
        tierCodes: ["STANDARD"],
        trialPeriodDays: 30,
        billingEventRulesSummary: "Free trial",
      },
    ];
    vi.spyOn(httpClient, "get").mockResolvedValue(mockOffers);

    const result = await listPublishedMarketplaceOffers("pkg-1");
    expect(httpClient.get).toHaveBeenCalledWith(
      "/api/public/marketplace/offers/published?packageId=pkg-1",
    );
    expect(result).toEqual(mockOffers);
  });
});
