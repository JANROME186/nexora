import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { DoctorsScreen } from "../components/screens/DoctorsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/peopleApi";
import type { Doctor } from "../api/types";

function ScopedDoctorsHarness({ children }: { children: ReactNode }) {
  return (
    <AdminScopeProvider>
      <ScopeSetter />
      {children}
    </AdminScopeProvider>
  );
}

function ScopeSetter() {
  const { setTenantId, setLaboratoryId } = useAdminScope();
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) {
      return;
    }
    initialized.current = true;
    setTenantId("tenant-1");
    setLaboratoryId("lab-1");
  }, [setLaboratoryId, setTenantId]);

  return null;
}

const registeredDoctor: Doctor = {
  doctorId: "doctor-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  doctorCode: "D-001",
  givenName: "Marie",
  familyName: "Curie",
  fullName: "Marie Curie",
  doctorType: "referring_external",
  primaryDocumentType: "professional_license",
  primaryDocumentNumberMasked: "****9999",
  status: "active",
  portalStatus: "not_provisioned",
  version: 1
};

describe("DoctorsScreen", () => {
  it("registers a doctor and suspends it after explicit confirmation", async () => {
    vi.spyOn(api, "registerDoctor").mockResolvedValue(registeredDoctor);
    vi.spyOn(api, "suspendDoctor").mockResolvedValue({
      ...registeredDoctor,
      status: "suspended"
    });

    const user = userEvent.setup();
    render(
      <ScopedDoctorsHarness>
        <DoctorsScreen />
      </ScopedDoctorsHarness>
    );

    await user.type(screen.getByLabelText("Doctor code"), "D-001");
    await user.type(screen.getByLabelText("Given name"), "Marie");
    await user.type(screen.getByLabelText("Family name"), "Curie");
    await user.type(screen.getByLabelText("Primary document number"), "MD-9999");
    await user.click(screen.getByRole("button", { name: "Register doctor" }));

    expect(await screen.findByText("Doctor registered.")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Selected doctor: doctor-1" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Suspend doctor" }));
    expect(screen.getByRole("dialog", { name: "Confirm doctor suspension" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(api.suspendDoctor).toHaveBeenCalledWith("doctor-1", undefined);
    expect(await screen.findByText("Doctor suspended.")).toBeInTheDocument();
  });
});
