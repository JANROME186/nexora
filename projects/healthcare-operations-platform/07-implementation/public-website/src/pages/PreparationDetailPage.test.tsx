import { screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { PreparationDetailPage } from "./PreparationDetailPage";

describe("PreparationDetailPage", () => {
  it("renders the preparation snapshot including instructions and duration", async () => {
    vi.spyOn(publicCatalogApi, "getPreparationSnapshot").mockResolvedValue({
      preparationId: "prep-1",
      code: "SERUM",
      titleEn: "Serum handling",
      titleEs: "Manejo de suero",
      instructionTextEn: "Keep refrigerated.",
      instructionTextEs: "Mantener refrigerado.",
      category: "SAMPLE_HANDLING",
      durationHours: 2,
      version: 1,
    });

    renderWithProviders(<PreparationDetailPage preparationId="prep-1" />);
    await waitFor(() =>
      expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent("Manejo de suero"),
    );
    expect(screen.getByText("Mantener refrigerado.")).toBeInTheDocument();
    expect(screen.getByText("2 horas")).toBeInTheDocument();
  });

  it("omits duration when null", async () => {
    vi.spyOn(publicCatalogApi, "getPreparationSnapshot").mockResolvedValue({
      preparationId: "prep-2",
      code: "EDTA",
      titleEn: "EDTA handling",
      titleEs: "Manejo de EDTA",
      instructionTextEn: "Mix gently.",
      instructionTextEs: "Mezclar suavemente.",
      category: "SAMPLE_HANDLING",
      durationHours: null,
      version: 1,
    });

    renderWithProviders(<PreparationDetailPage preparationId="prep-2" />);
    await waitFor(() => expect(screen.getByRole("heading", { level: 1 })).toBeInTheDocument());
    expect(screen.queryByText("Duración")).not.toBeInTheDocument();
  });
});
