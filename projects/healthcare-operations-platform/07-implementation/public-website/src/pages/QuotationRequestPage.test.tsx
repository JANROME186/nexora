import { fireEvent, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import * as careDeliveryApi from "../api/careDeliveryApi";
import { ApiError } from "../api/httpClient";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { QuotationRequestPage } from "./QuotationRequestPage";

function mockCatalogOptions() {
  vi.spyOn(publicCatalogApi, "listTests").mockResolvedValue([]);
  vi.spyOn(publicCatalogApi, "listPanels").mockResolvedValue([
    {
      panelId: "p-1",
      code: "CBC",
      nameEn: "Complete blood count",
      nameEs: "Biometria hematica",
      version: 1,
    },
  ]);
}

describe("QuotationRequestPage", () => {
  beforeEach(() => {
    mockCatalogOptions();
  });

  it("requires at least one selected line before submitting", async () => {
    const user = userEvent.setup();
    renderWithProviders(<QuotationRequestPage />);
    await user.type(screen.getByLabelText("Correo electrónico"), "someone@example.com");
    await user.click(screen.getByRole("checkbox"));
    await user.click(screen.getByRole("button", { name: "Enviar solicitud" }));
    expect(screen.getByText("Selecciona al menos una prueba o panel.")).toBeInTheDocument();
  });

  it("submits a quantity-bearing line and shows the success panel", async () => {
    const submitSpy = vi.spyOn(careDeliveryApi, "submitQuotationRequest").mockResolvedValue({
      quotationId: "quo-1",
      laboratoryId: "lab-local",
      branchId: "branch-local",
      status: "draft",
    });

    const user = userEvent.setup();
    renderWithProviders(<QuotationRequestPage />);

    await user.type(screen.getByLabelText("Correo electrónico"), "someone@example.com");
    await waitFor(() =>
      expect(screen.getByRole("option", { name: /Biometria hematica/ })).toBeInTheDocument(),
    );
    await user.selectOptions(screen.getByLabelText("Selecciona una prueba o panel"), [
      screen.getByRole("option", { name: /Biometria hematica/ }),
    ]);
    await user.click(screen.getByRole("button", { name: "Agregar prueba o panel" }));

    const quantityInput = screen.getByLabelText("Cantidad");
    fireEvent.change(quantityInput, { target: { value: "3" } });

    await user.click(screen.getByRole("checkbox"));
    await user.click(screen.getByRole("button", { name: "Enviar solicitud" }));

    await waitFor(() => expect(screen.getByText("Solicitud recibida")).toBeInTheDocument());
    expect(submitSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        lines: [{ testDefinitionId: "p-1", catalogItemKind: "panel", quantity: 3 }],
      }),
    );
  });

  it("resolves a generic error message for a non-rate-limit failure", async () => {
    vi.spyOn(careDeliveryApi, "submitQuotationRequest").mockRejectedValue(
      new ApiError(400, "PUBLIC_QUOTATION_REQUEST_INVALID", "invalid"),
    );

    const user = userEvent.setup();
    renderWithProviders(<QuotationRequestPage />);
    await user.type(screen.getByLabelText("Correo electrónico"), "someone@example.com");
    await waitFor(() =>
      expect(screen.getByRole("option", { name: /Biometria hematica/ })).toBeInTheDocument(),
    );
    await user.selectOptions(screen.getByLabelText("Selecciona una prueba o panel"), [
      screen.getByRole("option", { name: /Biometria hematica/ }),
    ]);
    await user.click(screen.getByRole("button", { name: "Agregar prueba o panel" }));
    await user.click(screen.getByRole("checkbox"));
    await user.click(screen.getByRole("button", { name: "Enviar solicitud" }));

    await waitFor(() =>
      expect(
        screen.getByText(
          "No pudimos procesar tu solicitud de cotización. Revisa los datos ingresados.",
        ),
      ).toBeInTheDocument(),
    );
  });
});
