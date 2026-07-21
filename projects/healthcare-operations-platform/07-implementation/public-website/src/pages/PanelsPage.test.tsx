import { screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { PanelsPage } from "./PanelsPage";

describe("PanelsPage", () => {
  it("renders published panels once loaded", async () => {
    vi.spyOn(publicCatalogApi, "listPanels").mockResolvedValue([
      {
        panelId: "p-1",
        code: "CBC",
        nameEn: "Complete blood count",
        nameEs: "Biometria hematica",
        version: 1,
      },
    ]);

    renderWithProviders(<PanelsPage />);
    await waitFor(() => expect(screen.getByText("Biometria hematica")).toBeInTheDocument());
  });

  it("renders an error state", async () => {
    vi.spyOn(publicCatalogApi, "listPanels").mockRejectedValue(new Error("boom"));
    renderWithProviders(<PanelsPage />);
    await waitFor(() => expect(screen.getByRole("alert")).toBeInTheDocument());
  });
});
