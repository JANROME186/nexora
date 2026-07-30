import { fireEvent, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicMarketplaceApi from "../api/publicMarketplaceApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { MarketplaceDetailPage } from "./MarketplaceDetailPage";

describe("MarketplaceDetailPage", () => {
  it("renders package details and offers once loaded", async () => {
    vi.spyOn(publicMarketplaceApi, "getPublishedMarketplacePackageSnapshot").mockResolvedValue({
      packageId: "pkg-1",
      code: "PKG_ANALYTICS",
      name: "Advanced Analytics Package",
      category: "analytics",
      capabilityMappings: ["BCM-CLI-005"],
      status: "published",
    });

    vi.spyOn(publicMarketplaceApi, "listPublishedMarketplaceOffers").mockResolvedValue([
      {
        offerId: "off-1",
        packageId: "pkg-1",
        packageVersion: "1.0.0",
        offerCode: "OFFER_ANALYTICS_PRO",
        offerType: "expansion_package",
        lifecycleStatus: "published",
        tierCodes: ["PRO"],
        trialPeriodDays: 14,
        billingEventRulesSummary: "Monthly subscription $99/mo",
      },
    ]);

    renderWithProviders(<MarketplaceDetailPage packageId="pkg-1" />);

    expect(screen.getByText(/Cargando/)).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText("Advanced Analytics Package")).toBeInTheDocument());
    expect(screen.getByText("PKG_ANALYTICS")).toBeInTheDocument();
    expect(screen.getByText("BCM-CLI-005")).toBeInTheDocument();
    expect(screen.getByText("OFFER_ANALYTICS_PRO")).toBeInTheDocument();
    expect(screen.getByText(/14/)).toBeInTheDocument();
  });

  it("handles commercial contact request submission", async () => {
    vi.spyOn(publicMarketplaceApi, "getPublishedMarketplacePackageSnapshot").mockResolvedValue({
      packageId: "pkg-1",
      code: "PKG_ANALYTICS",
      name: "Advanced Analytics Package",
      category: "analytics",
      capabilityMappings: ["BCM-CLI-005"],
      status: "published",
    });

    vi.spyOn(publicMarketplaceApi, "listPublishedMarketplaceOffers").mockResolvedValue([]);

    renderWithProviders(<MarketplaceDetailPage packageId="pkg-1" />);

    await waitFor(() => expect(screen.getByText("Advanced Analytics Package")).toBeInTheDocument());

    const submitBtn = screen.getByRole("button", { name: /Solicitar contacto comercial/ });
    fireEvent.click(submitBtn);

    await waitFor(() =>
      expect(screen.getByText(/Solicitud de contacto enviada/)).toBeInTheDocument(),
    );
  });

  it("renders error state with working retry functionality", async () => {
    const spyPkg = vi
      .spyOn(publicMarketplaceApi, "getPublishedMarketplacePackageSnapshot")
      .mockRejectedValue(new Error("fetch error"));
    const spyOffers = vi
      .spyOn(publicMarketplaceApi, "listPublishedMarketplaceOffers")
      .mockResolvedValue([]);

    renderWithProviders(<MarketplaceDetailPage packageId="pkg-1" />);

    await waitFor(() => expect(screen.getByRole("alert")).toBeInTheDocument());

    fireEvent.click(screen.getByRole("button", { name: /Reintentar/ }));
    await waitFor(() => expect(spyPkg).toHaveBeenCalledTimes(2));
    expect(spyOffers).toHaveBeenCalled();
  });
});
