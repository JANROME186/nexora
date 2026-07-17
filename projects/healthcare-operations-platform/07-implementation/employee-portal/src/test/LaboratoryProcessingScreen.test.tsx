import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { LaboratoryProcessingScreen } from "../components/screens/LaboratoryProcessingScreen";
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
  status: "captured",
  resultValues: [],
  incidents: [],
  analyteSnapshots: [],
  referenceRangeSnapshots: [],
  amendments: [],
  version: 1,
};

describe("LaboratoryProcessingScreen", () => {
  it("loads worklist and captures a result", async () => {
    vi.spyOn(api, "listProcessingWorklist").mockResolvedValue([mockResult]);
    vi.spyOn(api, "captureResult").mockResolvedValue({
      ...mockResult,
      resultValues: [{ rawValue: "120", capturedAt: "2026-07-17T00:00:00Z", capturedBy: "me" }],
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <LaboratoryProcessingScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    expect(await screen.findByText("Worklist loaded.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "result-1" }));
    expect(screen.getByRole("heading", { name: "Result detail: result-1" })).toBeInTheDocument();

    await user.type(screen.getByLabelText("Raw Value"), "120");
    await user.click(screen.getByRole("button", { name: "Capture" }));

    expect(await screen.findByText("Result captured.")).toBeInTheDocument();
    expect(api.captureResult).toHaveBeenCalledWith(
      "result-1",
      expect.objectContaining({
        values: [expect.objectContaining({ rawValue: "120" })],
      }),
    );
  });

  it("records an incident and submits for validation", async () => {
    vi.spyOn(api, "listProcessingWorklist").mockResolvedValue([mockResult]);
    vi.spyOn(api, "recordIncident").mockResolvedValue({ ...mockResult });
    vi.spyOn(api, "submitForValidation").mockResolvedValue({
      ...mockResult,
      status: "pending_technical_validation",
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <LaboratoryProcessingScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    await user.click(await screen.findByRole("button", { name: "result-1" }));

    await user.type(screen.getByLabelText("Description"), "Bad sample");
    await user.click(screen.getByRole("button", { name: "Record Incident" }));
    expect(await screen.findByText("Incident recorded.")).toBeInTheDocument();
    expect(api.recordIncident).toHaveBeenCalledWith(
      "result-1",
      expect.objectContaining({ description: "Bad sample" }),
    );

    await user.click(screen.getByRole("button", { name: "Submit for Validation" }));
    expect(await screen.findByText("Result submitted for validation.")).toBeInTheDocument();
    expect(api.submitForValidation).toHaveBeenCalledWith("result-1");
  });

  it("surfaces a business conflict when submission fails", async () => {
    vi.spyOn(api, "listProcessingWorklist").mockResolvedValue([mockResult]);
    vi.spyOn(api, "submitForValidation").mockRejectedValue(new ApiError(409, "NO_VALUES_CAPTURED"));

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <LaboratoryProcessingScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    await user.click(await screen.findByRole("button", { name: "result-1" }));

    await user.click(screen.getByRole("button", { name: "Submit for Validation" }));
    expect(await screen.findByText(/NO_VALUES_CAPTURED/)).toBeInTheDocument();
  });
});
