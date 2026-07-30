import { fireEvent, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicMarketplaceApi from "../api/publicMarketplaceApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { MarketplacePage } from "./MarketplacePage";

describe("MarketplacePage", () => {
  it("renders published marketplace packages once loaded", async () => {
    vi.spyOn(publicMarketplaceApi, "listPublishedMarketplacePackages").mockResolvedValue([
      {
        packageId: "pkg-1",
        code: "PKG_ANALYTICS",
        name: "Advanced Analytics Package",
        category: "analytics",
        capabilityMappings: ["BCM-CLI-005"],
        status: "published",
      },
    ]);

    renderWithProviders(<MarketplacePage />);

    expect(screen.getByText(/Cargando/)).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText("Advanced Analytics Package")).toBeInTheDocument());
    expect(document.title).toContain("Marketplace");
  });

  it("filters packages by search term and category", async () => {
    vi.spyOn(publicMarketplaceApi, "listPublishedMarketplacePackages").mockResolvedValue([
      {
        packageId: "pkg-1",
        code: "PKG_ANALYTICS",
        name: "Advanced Analytics Package",
        category: "analytics",
        capabilityMappings: ["BCM-CLI-005"],
        status: "published",
      },
      {
        packageId: "pkg-2",
        code: "PKG_CLINICAL",
        name: "Clinical Core Package",
        category: "clinical",
        capabilityMappings: ["BCM-CLI-001"],
        status: "published",
      },
    ]);

    renderWithProviders(<MarketplacePage />);

    await waitFor(() => expect(screen.getByText("Advanced Analytics Package")).toBeInTheDocument());
    expect(screen.getByText("Clinical Core Package")).toBeInTheDocument();

    const searchInput = screen.getByPlaceholderText(/Buscar por nombre/);
    fireEvent.change(searchInput, { target: { value: "Analytics" } });

    expect(screen.getByText("Advanced Analytics Package")).toBeInTheDocument();
    expect(screen.queryByText("Clinical Core Package")).not.toBeInTheDocument();

    fireEvent.change(searchInput, { target: { value: "" } });
    expect(screen.getByText("Clinical Core Package")).toBeInTheDocument();

    const categorySelect = screen.getByRole("combobox");
    fireEvent.change(categorySelect, { target: { value: "clinical" } });

    expect(screen.queryByText("Advanced Analytics Package")).not.toBeInTheDocument();
    expect(screen.getByText("Clinical Core Package")).toBeInTheDocument();
  });

  it("renders empty state when no packages match filter or list is empty", async () => {
    vi.spyOn(publicMarketplaceApi, "listPublishedMarketplacePackages").mockResolvedValue([]);

    renderWithProviders(<MarketplacePage />);

    await waitFor(() =>
      expect(screen.getByText(/No hay información disponible/)).toBeInTheDocument(),
    );
  });

  it("renders error state with retry functionality", async () => {
    const spy = vi
      .spyOn(publicMarketplaceApi, "listPublishedMarketplacePackages")
      .mockRejectedValueOnce(new Error("network failure"))
      .mockResolvedValueOnce([]);

    renderWithProviders(<MarketplacePage />);

    await waitFor(() => expect(screen.getByRole("alert")).toBeInTheDocument());

    fireEvent.click(screen.getByRole("button", { name: /Reintentar/ }));
    await waitFor(() => expect(spy).toHaveBeenCalledTimes(2));
  });
});
