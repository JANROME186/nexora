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
  version: 1,
};

describe("DoctorsScreen", () => {
  it("registers a doctor and suspends it after explicit confirmation", async () => {
    vi.spyOn(api, "registerDoctor").mockResolvedValue(registeredDoctor);
    vi.spyOn(api, "suspendDoctor").mockResolvedValue({
      ...registeredDoctor,
      status: "suspended",
    });

    const user = userEvent.setup();
    render(
      <ScopedDoctorsHarness>
        <DoctorsScreen />
      </ScopedDoctorsHarness>,
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

  it("updates and retires a selected doctor after explicit confirmation", async () => {
    vi.spyOn(api, "listDoctors").mockResolvedValue([registeredDoctor]);
    vi.spyOn(api, "updateDoctor").mockResolvedValue({
      ...registeredDoctor,
      familyName: "Sklodowska",
      fullName: "Marie Sklodowska",
      version: 2,
    });
    vi.spyOn(api, "retireDoctor").mockResolvedValue({
      ...registeredDoctor,
      status: "retired",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedDoctorsHarness>
        <DoctorsScreen />
      </ScopedDoctorsHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load doctors" }));
    await user.click(await screen.findByRole("button", { name: "doctor-1" }));

    await user.clear(
      screen.getByLabelText("Family name", { selector: "#edit-doctor-family-name" }),
    );
    await user.type(
      screen.getByLabelText("Family name", { selector: "#edit-doctor-family-name" }),
      "Sklodowska",
    );
    await user.type(
      screen.getByLabelText("Primary document number", {
        selector: "#edit-doctor-document-number",
      }),
      "MD-9999",
    );
    await user.click(screen.getByRole("button", { name: "Save doctor" }));

    expect(api.updateDoctor).toHaveBeenCalledWith(
      "doctor-1",
      expect.objectContaining({ familyName: "Sklodowska" }),
    );
    expect(await screen.findByText("Doctor updated.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Retire doctor" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(api.retireDoctor).toHaveBeenCalledWith("doctor-1");
    expect(await screen.findByText("Doctor retired.")).toBeInTheDocument();
  });

  it("assigns and unassigns a doctor specialty after explicit confirmation", async () => {
    vi.spyOn(api, "listDoctors").mockResolvedValue([registeredDoctor]);
    vi.spyOn(api, "listSpecialtyAssignments").mockResolvedValue([]);
    vi.spyOn(api, "assignSpecialty").mockResolvedValue({
      assignmentId: "assignment-1",
      doctorId: "doctor-1",
      specialtyCode: "cardiology",
      primary: true,
    });
    vi.spyOn(api, "unassignSpecialty").mockResolvedValue(undefined);

    const user = userEvent.setup();
    render(
      <ScopedDoctorsHarness>
        <DoctorsScreen />
      </ScopedDoctorsHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load doctors" }));
    await user.click(await screen.findByRole("button", { name: "doctor-1" }));

    await user.click(screen.getByRole("button", { name: "Load specialties" }));
    expect(await screen.findByText("Specialties loaded.")).toBeInTheDocument();

    await user.type(screen.getByLabelText("Specialty code"), "cardiology");
    await user.click(screen.getByLabelText("Primary specialty"));
    await user.click(screen.getByRole("button", { name: "Assign specialty" }));

    expect(api.assignSpecialty).toHaveBeenCalledWith("doctor-1", {
      specialtyCode: "cardiology",
      primary: true,
    });
    expect(await screen.findByText("Specialty assigned.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Unassign" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(api.unassignSpecialty).toHaveBeenCalledWith("doctor-1", "assignment-1");
    expect(await screen.findByText("Specialty unassigned.")).toBeInTheDocument();
  });
});
