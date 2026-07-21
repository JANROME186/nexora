import { screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { PanelDetailPage } from "./PanelDetailPage";

describe("PanelDetailPage", () => {
  it("renders the panel snapshot", async () => {
    vi.spyOn(publicCatalogApi, "getPanelSnapshot").mockResolvedValue({
      panelId: "p-1",
      code: "CBC",
      nameEn: "Complete blood count",
      nameEs: "Biometria hematica",
      version: 2,
    });

    renderWithProviders(<PanelDetailPage panelId="p-1" />);
    await waitFor(() =>
      expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent("Biometria hematica"),
    );
    expect(screen.getByText("2")).toBeInTheDocument();
  });
});
