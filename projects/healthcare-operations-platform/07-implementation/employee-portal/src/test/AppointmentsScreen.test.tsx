import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AppointmentsScreen } from "../components/screens/AppointmentsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/frontDeskApi";
import type { AppointmentSlot } from "../api/types";

function ScopedHarness({ children }: { children: ReactNode }) {
  return (
    <AdminScopeProvider>
      <ScopeSetter />
      {children}
    </AdminScopeProvider>
  );
}

function ScopeSetter() {
  const { setTenantId, setLaboratoryId, setBranchId } = useAdminScope();
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) {
      return;
    }
    initialized.current = true;
    setTenantId("tenant-1");
    setLaboratoryId("lab-1");
    setBranchId("branch-1");
  }, [setBranchId, setLaboratoryId, setTenantId]);

  return null;
}

const requested: AppointmentSlot = {
  appointmentId: "apt-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  patientId: "patient-1",
  scheduledStart: "2027-06-01",
  scheduledEnd: "2027-06-01",
  channel: "employee_portal",
  status: "requested",
  version: 1,
};

describe("AppointmentsScreen", () => {
  it("requests an appointment and confirms it after selection", async () => {
    vi.spyOn(api, "requestAppointment").mockResolvedValue(requested);
    vi.spyOn(api, "confirmAppointment").mockResolvedValue({
      ...requested,
      status: "confirmed",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <AppointmentsScreen />
      </ScopedHarness>,
    );

    await user.type(screen.getByLabelText("Patient id"), "patient-1");
    await user.click(screen.getByRole("button", { name: "Request appointment" }));

    expect(api.requestAppointment).toHaveBeenCalledWith(
      expect.objectContaining({
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        branchId: "branch-1",
        patientId: "patient-1",
        channel: "employee_portal",
      }),
    );
    expect(await screen.findByText("Appointment requested.")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Appointment detail: apt-1" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Confirm appointment" }));

    expect(api.confirmAppointment).toHaveBeenCalledWith("apt-1");
    expect(await screen.findByText("Appointment confirmed.")).toBeInTheDocument();
  });

  it("checks in a confirmed appointment", async () => {
    vi.spyOn(api, "listAppointments").mockResolvedValue([{ ...requested, status: "confirmed" }]);
    vi.spyOn(api, "checkInAppointment").mockResolvedValue({
      ...requested,
      status: "checked_in",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <AppointmentsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load appointments" }));
    await user.click(await screen.findByRole("button", { name: "apt-1" }));

    await user.click(screen.getByRole("button", { name: "Check in" }));
    expect(api.checkInAppointment).toHaveBeenCalledWith("apt-1");
    expect(await screen.findByText("Patient checked in.")).toBeInTheDocument();
  });

  it("cancels a confirmed appointment with a reason code after explicit confirmation", async () => {
    vi.spyOn(api, "listAppointments").mockResolvedValue([{ ...requested, status: "confirmed" }]);
    vi.spyOn(api, "cancelAppointment").mockResolvedValue({
      ...requested,
      status: "cancelled",
      cancellationReason: "patient_request",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <AppointmentsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load appointments" }));
    await user.click(await screen.findByRole("button", { name: "apt-1" }));
    await user.type(screen.getByLabelText("Reason code (optional)"), "patient_request");
    await user.click(screen.getByRole("button", { name: "Cancel appointment" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(api.cancelAppointment).toHaveBeenCalledWith("apt-1", { reasonCode: "patient_request" });
    expect(await screen.findByText("Appointment cancelled.")).toBeInTheDocument();
  });

  it("shows an empty state when there are no appointments", async () => {
    vi.spyOn(api, "listAppointments").mockResolvedValue([]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <AppointmentsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load appointments" }));
    expect(
      await screen.findByText("No appointments exist yet for this tenant."),
    ).toBeInTheDocument();
  });
});
