import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PublicAppointmentRequestsScreen } from "../components/screens/PublicAppointmentRequestsScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/publicRequestsApi";
import type { AppointmentSlot } from "../api/types";

function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>{children}</AdminScopeProvider>
    </LocaleProvider>
  );
}

function ScopedHarness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>
        <ScopeSetter />
        {children}
      </AdminScopeProvider>
    </LocaleProvider>
  );
}

function ScopeSetter() {
  const { setTenantId } = useAdminScope();
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) {
      return;
    }
    initialized.current = true;
    setTenantId("tenant-1");
  }, [setTenantId]);

  return null;
}

const publicRequested: AppointmentSlot = {
  appointmentId: "apt-public-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  scheduledStart: "2027-06-01",
  scheduledEnd: "2027-06-01",
  channel: "public_website",
  status: "requested",
  prospectiveFullName: "Ada Lovelace",
  prospectivePhone: "555-1234",
  version: 1,
};

const staffAppointment: AppointmentSlot = {
  appointmentId: "apt-staff-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  patientId: "patient-1",
  scheduledStart: "2027-06-02",
  scheduledEnd: "2027-06-02",
  channel: "employee_portal",
  status: "requested",
  version: 1,
};

describe("PublicAppointmentRequestsScreen", () => {
  it("requires a tenant scope before loading", () => {
    render(
      <Harness>
        <PublicAppointmentRequestsScreen />
      </Harness>,
    );

    expect(screen.getByText("Selecciona un tenant antes de continuar.")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Cargar solicitudes" })).toBeDisabled();
  });

  it("loads the queue and filters out non-public or already-actioned appointments", async () => {
    vi.spyOn(api, "listAppointments").mockResolvedValue([publicRequested, staffAppointment]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicAppointmentRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));

    expect(await screen.findByText("Solicitudes cargadas.")).toBeInTheDocument();
    expect(screen.getByText("Ada Lovelace")).toBeInTheDocument();
    expect(screen.queryByText("apt-staff-1")).not.toBeInTheDocument();
  });

  it("shows the empty state when there are no pending public appointment requests", async () => {
    vi.spyOn(api, "listAppointments").mockResolvedValue([staffAppointment]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicAppointmentRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));

    expect(
      await screen.findByText("No hay solicitudes públicas de cita pendientes."),
    ).toBeInTheDocument();
  });

  it("confirms a selected request only after the confirmation dialog is accepted", async () => {
    vi.spyOn(api, "listAppointments").mockResolvedValue([publicRequested]);
    vi.spyOn(api, "confirmAppointment").mockResolvedValue({
      ...publicRequested,
      status: "confirmed",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicAppointmentRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));
    await user.click(await screen.findByRole("button", { name: "apt-public-1" }));
    await user.click(screen.getByRole("button", { name: "Confirmar cita" }));

    expect(screen.getByRole("dialog", { name: "Confirmar cita" })).toBeInTheDocument();
    expect(api.confirmAppointment).not.toHaveBeenCalled();

    await user.click(screen.getAllByRole("button", { name: "Confirmar" }).slice(-1)[0]);

    expect(await screen.findByText("Cita confirmada.")).toBeInTheDocument();
    expect(api.confirmAppointment).toHaveBeenCalledWith("apt-public-1");
    expect(screen.queryByRole("button", { name: "apt-public-1" })).not.toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "confirmed" })).toBeInTheDocument();
  });

  it("rejects a selected request with a reason code after confirmation", async () => {
    vi.spyOn(api, "listAppointments").mockResolvedValue([publicRequested]);
    vi.spyOn(api, "cancelAppointment").mockResolvedValue({
      ...publicRequested,
      status: "cancelled",
      cancellationReason: "duplicate",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicAppointmentRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));
    await user.click(await screen.findByRole("button", { name: "apt-public-1" }));
    await user.type(screen.getByLabelText("Motivo de rechazo"), "duplicate");
    await user.click(screen.getByRole("button", { name: "Rechazar solicitud" }));
    await user.click(screen.getAllByRole("button", { name: "Confirmar" }).slice(-1)[0]);

    expect(await screen.findByText("Solicitud rechazada.")).toBeInTheDocument();
    expect(api.cancelAppointment).toHaveBeenCalledWith("apt-public-1", { reasonCode: "duplicate" });
  });

  it("surfaces an error when loading the queue fails", async () => {
    vi.spyOn(api, "listAppointments").mockRejectedValue(
      new ApiError(500, "Unexpected failure while loading appointments."),
    );

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicAppointmentRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));

    expect(
      await screen.findByText("Unexpected failure while loading appointments."),
    ).toBeInTheDocument();
  });
});
