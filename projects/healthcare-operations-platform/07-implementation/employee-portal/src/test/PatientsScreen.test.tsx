import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PatientsScreen } from "../components/screens/PatientsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/peopleApi";

function ScopedPatientsHarness({ children }: { children: ReactNode }) {
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

describe("PatientsScreen", () => {
  it("registers a patient in the active laboratory scope and lists it", async () => {
    vi.spyOn(api, "registerPatient").mockResolvedValue({
      patientId: "patient-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      patientCode: "P-001",
      givenName: "Ada",
      familyName: "Lovelace",
      fullName: "Ada Lovelace",
      birthDate: "1990-01-01",
      sexAtBirth: "female",
      primaryDocumentType: "national_id",
      primaryDocumentNumberMasked: "****1234",
      status: "active",
      version: 1,
    });
    vi.spyOn(api, "listPatients").mockResolvedValue([
      {
        patientId: "patient-1",
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        patientCode: "P-001",
        fullName: "Ada Lovelace",
        status: "active",
        version: 1,
      },
    ]);

    const user = userEvent.setup();
    render(
      <ScopedPatientsHarness>
        <PatientsScreen />
      </ScopedPatientsHarness>,
    );

    await user.type(screen.getByLabelText("Patient code"), "P-001");
    await user.type(screen.getByLabelText("Given name"), "Ada");
    await user.type(screen.getByLabelText("Family name"), "Lovelace");
    await user.type(screen.getByLabelText("Primary document number"), "DOC-1234");
    await user.click(screen.getByRole("button", { name: "Register patient" }));

    expect(api.registerPatient).toHaveBeenCalledWith(
      expect.objectContaining({
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        patientCode: "P-001",
        givenName: "Ada",
        familyName: "Lovelace",
        primaryDocumentNumber: "DOC-1234",
      }),
    );
    expect(await screen.findByText("Patient registered.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Load patients" }));
    expect(await screen.findByText("Patients loaded.")).toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "P-001" })).toBeInTheDocument();
  });

  it("shows an empty state and no patient rows when the laboratory has none", async () => {
    vi.spyOn(api, "listPatients").mockResolvedValue([]);

    const user = userEvent.setup();
    render(
      <ScopedPatientsHarness>
        <PatientsScreen />
      </ScopedPatientsHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load patients" }));
    expect(
      await screen.findByText("No patients registered yet in this laboratory."),
    ).toBeInTheDocument();
  });
});
