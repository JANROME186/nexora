import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import * as careDeliveryApi from "../api/careDeliveryApi";
import { ApiError } from "../api/httpClient";
import * as publicCatalogApi from "../api/publicCatalogApi";
import { renderWithProviders } from "../test/renderWithProviders";
import { AppointmentRequestPage } from "./AppointmentRequestPage";

function mockCatalogOptions() {
  vi.spyOn(publicCatalogApi, "listTests").mockResolvedValue([
    {
      testDefinitionId: "t-1",
      code: "GLU",
      nameEn: "Fasting glucose",
      nameEs: "Glucosa en ayuno",
      methodology: "Enzymatic",
      measurementUnit: "mg/dL",
      resultType: "NUMERIC",
      turnaroundTimeHours: 4,
      version: 1,
    },
  ]);
  vi.spyOn(publicCatalogApi, "listPanels").mockResolvedValue([]);
}

async function fillContactAndItem(user: ReturnType<typeof userEvent.setup>) {
  await user.type(screen.getByLabelText("Teléfono"), "555-0100");
  await waitFor(() =>
    expect(screen.getByRole("option", { name: /Glucosa en ayuno/ })).toBeInTheDocument(),
  );
  await user.selectOptions(screen.getByLabelText("Selecciona una prueba o panel"), [
    screen.getByRole("option", { name: /Glucosa en ayuno/ }),
  ]);
  await user.click(screen.getByRole("button", { name: "Agregar prueba o panel" }));
  await user.type(screen.getByLabelText("Fecha deseada (inicio)"), "2026-08-01T10:00");
  await user.type(screen.getByLabelText("Fecha deseada (fin)"), "2026-08-01T10:30");
  await user.click(screen.getByRole("checkbox"));
}

describe("AppointmentRequestPage", () => {
  beforeEach(() => {
    mockCatalogOptions();
  });

  it("shows a validation message when no contact method is provided", async () => {
    const user = userEvent.setup();
    renderWithProviders(<AppointmentRequestPage />);
    await user.click(screen.getByRole("button", { name: "Enviar solicitud" }));
    expect(
      screen.getByText("Proporciona al menos un teléfono o correo electrónico."),
    ).toBeInTheDocument();
  });

  it("submits successfully and allows submitting another request", async () => {
    const submitSpy = vi.spyOn(careDeliveryApi, "submitAppointmentRequest").mockResolvedValue({
      appointmentId: "apt-1",
      laboratoryId: "lab-local",
      branchId: "branch-local",
      scheduledStart: "2026-08-01T10:00:00.000Z",
      scheduledEnd: "2026-08-01T10:30:00.000Z",
      status: "requested",
      channel: "public_website",
    });

    const user = userEvent.setup();
    renderWithProviders(<AppointmentRequestPage />);
    await fillContactAndItem(user);
    await user.click(screen.getByRole("button", { name: "Enviar solicitud" }));

    await waitFor(() => expect(screen.getByText("Solicitud recibida")).toBeInTheDocument());
    expect(submitSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        tenantId: "tenant-local",
        laboratoryId: "lab-local",
        prospectivePhone: "555-0100",
        requestedItems: [{ testDefinitionId: "t-1", catalogItemKind: "test" }],
      }),
    );

    await user.click(screen.getByRole("button", { name: "Enviar otra solicitud" }));
    expect(screen.getByRole("button", { name: "Enviar solicitud" })).toBeInTheDocument();
  });

  it("starts a cooldown and disables the submit button on a 429 response", async () => {
    vi.spyOn(careDeliveryApi, "submitAppointmentRequest").mockRejectedValue(
      new ApiError(429, "PUBLIC_RATE_LIMIT_EXCEEDED", "slow down"),
    );

    const user = userEvent.setup();
    renderWithProviders(<AppointmentRequestPage />);
    await fillContactAndItem(user);
    await user.click(screen.getByRole("button", { name: "Enviar solicitud" }));

    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Enviar solicitud" })).toBeDisabled(),
    );
    expect(screen.getByRole("status")).toHaveTextContent(/Hemos recibido demasiadas solicitudes/);
    expect(screen.getByRole("alert")).toHaveTextContent(/Hemos recibido demasiadas solicitudes/);
  });
});
