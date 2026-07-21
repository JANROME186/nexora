import { fireEvent, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { ServicesPage } from "./ServicesPage";

describe("ServicesPage", () => {
  it("renders published diagnostic services once loaded", async () => {
    vi.spyOn(publicCatalogApi, "listDiagnosticServices").mockResolvedValue([
      {
        serviceId: "svc-1",
        code: "SVC_GLU",
        nameEn: "Fasting glucose",
        nameEs: "Glucosa en ayuno",
        serviceType: "LAB_TEST",
        version: 1,
      },
    ]);

    renderWithProviders(<ServicesPage />);

    expect(screen.getByText(/Cargando/)).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText("Glucosa en ayuno")).toBeInTheDocument());
    expect(document.title).toContain("Servicios diagnósticos");
  });

  it("renders an empty state when there are no published services", async () => {
    vi.spyOn(publicCatalogApi, "listDiagnosticServices").mockResolvedValue([]);
    renderWithProviders(<ServicesPage />);
    await waitFor(() =>
      expect(screen.getByText(/No hay información disponible/)).toBeInTheDocument(),
    );
  });

  it("renders an error state with a working retry button", async () => {
    const spy = vi
      .spyOn(publicCatalogApi, "listDiagnosticServices")
      .mockRejectedValueOnce(new Error("network down"))
      .mockResolvedValueOnce([]);

    renderWithProviders(<ServicesPage />);
    await waitFor(() => expect(screen.getByRole("alert")).toBeInTheDocument());

    fireEvent.click(screen.getByRole("button", { name: /Reintentar/ }));
    await waitFor(() => expect(spy).toHaveBeenCalledTimes(2));
  });
});
