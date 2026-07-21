import { screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { ServiceDetailPage } from "./ServiceDetailPage";

describe("ServiceDetailPage", () => {
  it("renders the localized service snapshot", async () => {
    vi.spyOn(publicCatalogApi, "getDiagnosticServiceSnapshot").mockResolvedValue({
      serviceId: "svc-1",
      code: "SVC_GLU",
      nameEn: "Fasting glucose",
      nameEs: "Glucosa en ayuno",
      serviceType: "LAB_TEST",
      version: 1,
    });

    renderWithProviders(<ServiceDetailPage serviceId="svc-1" />);
    await waitFor(() =>
      expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent("Glucosa en ayuno"),
    );
    expect(screen.getByText("SVC_GLU")).toBeInTheDocument();
    expect(screen.getByText("LAB_TEST")).toBeInTheDocument();
  });

  it("renders a not-published error message on 404", async () => {
    const { ApiError } = await import("../api/httpClient");
    vi.spyOn(publicCatalogApi, "getDiagnosticServiceSnapshot").mockRejectedValue(
      new ApiError(404, "PUBLIC_CATALOG_NOT_PUBLISHED", "not found"),
    );

    renderWithProviders(<ServiceDetailPage serviceId="missing" />);
    await waitFor(() =>
      expect(screen.getByText(/ya no está publicado o no existe/)).toBeInTheDocument(),
    );
  });
});
