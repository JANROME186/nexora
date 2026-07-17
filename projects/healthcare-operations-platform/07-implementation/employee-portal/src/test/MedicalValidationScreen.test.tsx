import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MedicalValidationScreen } from "../components/screens/MedicalValidationScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/laboratoryOperationsApi";
import type { LaboratoryResult } from "../api/types";

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
    if (initialized.current) return;
    initialized.current = true;
    setTenantId("tenant-1");
    setLaboratoryId("lab-1");
    setBranchId("branch-1");
  }, [setBranchId, setLaboratoryId, setTenantId]);
  return null;
}

const mockResult: LaboratoryResult = {
  resultId: "result-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  sampleId: "sample-1",
  testDefinitionId: "test-def-1",
  status: "pending_medical_validation",
  resultValues: [{ rawValue: "120", capturedAt: "2026-07-17T00:00:00Z", capturedBy: "me" }],
  incidents: [],
  analyteSnapshots: [],
  referenceRangeSnapshots: [],
  amendments: [],
  technicalValidation: {
    validatedAt: "2026-07-17T00:01:00Z",
    validatedBy: "tech",
    notes: "tech notes",
  },
  version: 1,
};

describe("MedicalValidationScreen", () => {
  it("loads worklist and medically validates a result", async () => {
    vi.spyOn(api, "listMedicalValidationWorklist").mockResolvedValue([mockResult]);
    vi.spyOn(api, "validateMedically").mockResolvedValue({
      ...mockResult,
      status: "medically_validated",
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <MedicalValidationScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    expect(await screen.findByText("Worklist loaded.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "result-1" }));
    expect(screen.getByRole("heading", { name: "Result detail: result-1" })).toBeInTheDocument();

    await user.type(screen.getByLabelText(/Clinical Interpretation Note/), "Looks normal");
    await user.click(screen.getByRole("button", { name: "Medically Validate" }));

    expect(api.validateMedically).toHaveBeenCalledWith("result-1", { notes: "Looks normal" });
    expect(await screen.findAllByText("medically_validated")).not.toHaveLength(0);
  });

  it("surfaces a business conflict when validation fails", async () => {
    vi.spyOn(api, "listMedicalValidationWorklist").mockResolvedValue([mockResult]);
    vi.spyOn(api, "validateMedically").mockRejectedValue(new ApiError(409, "INVALID_STATE"));

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <MedicalValidationScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    await user.click(await screen.findByRole("button", { name: "result-1" }));

    await user.click(screen.getByRole("button", { name: "Medically Validate" }));
    expect(await screen.findByText(/INVALID_STATE/)).toBeInTheDocument();
  });
});
