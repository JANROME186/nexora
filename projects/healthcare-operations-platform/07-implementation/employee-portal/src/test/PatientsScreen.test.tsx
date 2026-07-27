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

  const existingPatient = {
    patientId: "patient-1",
    tenantId: "tenant-1",
    laboratoryId: "lab-1",
    patientCode: "P-001",
    givenName: "Ada",
    familyName: "Lovelace",
    fullName: "Ada Lovelace",
    sexAtBirth: "female",
    primaryDocumentType: "national_id",
    primaryDocumentNumberMasked: "****1234",
    status: "active",
    version: 1,
  };

  it("updates and deactivates a selected patient after explicit confirmation", async () => {
    vi.spyOn(api, "listPatients").mockResolvedValue([existingPatient]);
    vi.spyOn(api, "updatePatient").mockResolvedValue({
      ...existingPatient,
      familyName: "Byron",
      fullName: "Ada Byron",
      version: 2,
    });
    vi.spyOn(api, "deactivatePatient").mockResolvedValue({
      ...existingPatient,
      status: "inactive",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedPatientsHarness>
        <PatientsScreen />
      </ScopedPatientsHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load patients" }));
    await user.click(await screen.findByRole("button", { name: "patient-1" }));

    await user.clear(
      screen.getByLabelText("Family name", { selector: "#edit-patient-family-name" }),
    );
    await user.type(
      screen.getByLabelText("Family name", { selector: "#edit-patient-family-name" }),
      "Byron",
    );
    await user.type(
      screen.getByLabelText("Primary document number", {
        selector: "#edit-patient-document-number",
      }),
      "DOC-1234",
    );
    await user.click(screen.getByRole("button", { name: "Save patient" }));

    expect(api.updatePatient).toHaveBeenCalledWith(
      "patient-1",
      expect.objectContaining({ familyName: "Byron" }),
    );
    expect(await screen.findByText("Patient updated.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Deactivate patient" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(api.deactivatePatient).toHaveBeenCalledWith("patient-1");
    expect(await screen.findByText("Patient deactivated.")).toBeInTheDocument();
  });

  it("attaches and removes a patient document after explicit confirmation", async () => {
    vi.spyOn(api, "listPatients").mockResolvedValue([existingPatient]);
    vi.spyOn(api, "listPatientDocuments").mockResolvedValue([]);
    vi.spyOn(api, "attachPatientDocument").mockResolvedValue({
      documentId: "document-1",
      patientId: "patient-1",
      category: "identification",
      fileReference: "file-1",
    });
    vi.spyOn(api, "removePatientDocument").mockResolvedValue(undefined);

    const user = userEvent.setup();
    render(
      <ScopedPatientsHarness>
        <PatientsScreen />
      </ScopedPatientsHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load patients" }));
    await user.click(await screen.findByRole("button", { name: "patient-1" }));

    await user.click(screen.getByRole("button", { name: "Load documents" }));
    expect(await screen.findByText("Documents loaded.")).toBeInTheDocument();

    await user.type(screen.getByLabelText("File reference"), "file-1");
    await user.click(screen.getByRole("button", { name: "Attach document" }));

    expect(api.attachPatientDocument).toHaveBeenCalledWith(
      "patient-1",
      expect.objectContaining({ category: "identification", fileReference: "file-1" }),
    );
    expect(await screen.findByText("Document attached.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Remove" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(api.removePatientDocument).toHaveBeenCalledWith("patient-1", "document-1");
    expect(await screen.findByText("Document removed.")).toBeInTheDocument();
  });
});
