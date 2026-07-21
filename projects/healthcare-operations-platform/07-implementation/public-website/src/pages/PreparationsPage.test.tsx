import { screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { PreparationsPage } from "./PreparationsPage";

describe("PreparationsPage", () => {
  it("renders published preparations once loaded", async () => {
    vi.spyOn(publicCatalogApi, "listPreparations").mockResolvedValue([
      {
        preparationId: "prep-1",
        code: "SERUM",
        titleEn: "Serum handling",
        titleEs: "Manejo de suero",
        instructionTextEn: "Keep refrigerated.",
        instructionTextEs: "Mantener refrigerado.",
        category: "SAMPLE_HANDLING",
        durationHours: 2,
        version: 1,
      },
    ]);

    renderWithProviders(<PreparationsPage />);
    await waitFor(() => expect(screen.getByText("Manejo de suero")).toBeInTheDocument());
  });

  it("renders an empty state when there are no published preparations", async () => {
    vi.spyOn(publicCatalogApi, "listPreparations").mockResolvedValue([]);
    renderWithProviders(<PreparationsPage />);
    await waitFor(() =>
      expect(screen.getByText(/No hay información disponible/)).toBeInTheDocument(),
    );
  });
});
