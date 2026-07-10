import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PatientRegistrationsScreen } from "../components/screens/PatientRegistrationsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/peopleApi";
import type { PatientRegistrationRequestRecord } from "../api/types";

function ScopedRegistrationsHarness({ children }: { children: ReactNode }) {
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

const pendingRegistration: PatientRegistrationRequestRecord = {
  registrationRequestId: "registration-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  intakeChannel: "walk_in",
  registrationKind: "new_patient",
  draftGivenName: "Rosalind",
  draftFamilyName: "Franklin",
  birthDate: "1920-07-25",
  draftDocumentType: "national_id",
  draftDocumentNumber: "DOC-EXISTING",
  outcome: "pending"
};

describe("PatientRegistrationsScreen", () => {
  it("starts a registration and shows visual duplicate candidates on a 409 match-resolution conflict", async () => {
    vi.spyOn(api, "startPatientRegistration").mockResolvedValue(pendingRegistration);
    vi.spyOn(api, "commitPatientRegistration").mockRejectedValue(
      new ApiError(
        409,
        "REGISTRATION_MATCH_RESOLUTION_REQUIRED: a high-confidence duplicate candidate exists; " +
          "resubmit with resolvedExistingPatientId to reuse the existing record or confirm a new patient is intended."
      )
    );
    vi.spyOn(api, "detectPersonDuplicates").mockResolvedValue([
      {
        personKind: "patient",
        sourceAggregateId: "patient-existing-1",
        fullName: "Rosalind Franklin",
        confidence: 0.95,
        matchReason: "family_name+given_name+birth_date+document_number"
      }
    ]);

    const user = userEvent.setup();
    render(
      <ScopedRegistrationsHarness>
        <PatientRegistrationsScreen />
      </ScopedRegistrationsHarness>
    );

    await user.type(screen.getByLabelText("Given name"), "Rosalind");
    await user.type(screen.getByLabelText("Family name"), "Franklin");
    await user.type(screen.getByLabelText("Document number"), "DOC-EXISTING");
    await user.click(screen.getByRole("button", { name: "Start registration" }));

    expect(await screen.findByText("Registration started (pending).")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Registration detail: registration-1" })).toBeInTheDocument();

    await user.type(screen.getByLabelText("New patient code"), "P-SHOULD-NOT-BE-CREATED");
    await user.click(
      screen.getByLabelText("Data processing consent granted (mandatory for this tenant)")
    );
    await user.click(screen.getByRole("button", { name: "Commit registration" }));

    expect(await screen.findByText(/REGISTRATION_MATCH_RESOLUTION_REQUIRED/)).toBeInTheDocument();
    expect(api.detectPersonDuplicates).toHaveBeenCalledWith(
      expect.objectContaining({ tenantId: "tenant-1", personKind: "patient", familyName: "Franklin", givenName: "Rosalind" })
    );

    expect(await screen.findByText("High-confidence duplicate candidates")).toBeInTheDocument();
    expect(screen.getByText("95%")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Use this patient" }));
    expect(screen.getByLabelText("Resolved existing patient id (leave blank for a new patient)")).toHaveValue(
      "patient-existing-1"
    );
  });
});
