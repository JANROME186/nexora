import { screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { TestsPage } from "./TestsPage";

describe("TestsPage", () => {
  it("renders published tests once loaded", async () => {
    vi.spyOn(publicCatalogApi, "listTests").mockResolvedValue([
      {
        testDefinitionId: "t-1",
        code: "GLU_FASTING",
        nameEn: "Fasting glucose",
        nameEs: "Glucosa en ayuno",
        methodology: "Enzymatic colorimetric",
        measurementUnit: "mg/dL",
        resultType: "NUMERIC",
        turnaroundTimeHours: 4,
        version: 1,
      },
    ]);

    renderWithProviders(<TestsPage />);
    await waitFor(() => expect(screen.getByText("Glucosa en ayuno")).toBeInTheDocument());
  });

  it("renders an empty state when there are no published tests", async () => {
    vi.spyOn(publicCatalogApi, "listTests").mockResolvedValue([]);
    renderWithProviders(<TestsPage />);
    await waitFor(() =>
      expect(screen.getByText(/No hay información disponible/)).toBeInTheDocument(),
    );
  });
});
