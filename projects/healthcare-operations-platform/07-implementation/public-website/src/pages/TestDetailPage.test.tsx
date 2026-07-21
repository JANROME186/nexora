import { screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { TestDetailPage } from "./TestDetailPage";

describe("TestDetailPage", () => {
  it("renders the test snapshot fields", async () => {
    vi.spyOn(publicCatalogApi, "getTestSnapshot").mockResolvedValue({
      testDefinitionId: "t-1",
      code: "GLU_FASTING",
      nameEn: "Fasting glucose",
      nameEs: "Glucosa en ayuno",
      methodology: "Enzymatic colorimetric",
      measurementUnit: "mg/dL",
      resultType: "NUMERIC",
      turnaroundTimeHours: 4,
      version: 1,
    });

    renderWithProviders(<TestDetailPage testId="t-1" />);
    await waitFor(() =>
      expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent("Glucosa en ayuno"),
    );
    expect(screen.getByText("Enzymatic colorimetric")).toBeInTheDocument();
    expect(screen.getByText("mg/dL")).toBeInTheDocument();
    expect(screen.getByText("4 horas")).toBeInTheDocument();
  });

  it("omits a null measurement unit", async () => {
    vi.spyOn(publicCatalogApi, "getTestSnapshot").mockResolvedValue({
      testDefinitionId: "t-2",
      code: "CBC",
      nameEn: "Complete blood count",
      nameEs: "Biometria hematica",
      methodology: "Automated hematology",
      measurementUnit: null,
      resultType: "PANEL",
      turnaroundTimeHours: 8,
      version: 1,
    });

    renderWithProviders(<TestDetailPage testId="t-2" />);
    await waitFor(() => expect(screen.getByRole("heading", { level: 1 })).toBeInTheDocument());
    expect(screen.queryByText("Unidad de medida")).not.toBeInTheDocument();
  });
});
