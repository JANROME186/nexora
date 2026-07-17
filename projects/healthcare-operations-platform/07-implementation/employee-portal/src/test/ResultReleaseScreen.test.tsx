import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ResultReleaseScreen } from "../components/screens/ResultReleaseScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
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
  status: "medically_validated",
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
  medicalValidation: {
    validatedAt: "2026-07-17T00:02:00Z",
    validatedBy: "doc",
    notes: "doc notes",
  },
  version: 1,
};

describe("ResultReleaseScreen", () => {
  it("loads worklist and releases a result", async () => {
    vi.spyOn(api, "listReleaseWorklist").mockResolvedValue([mockResult]);
    vi.spyOn(api, "releaseResult").mockResolvedValue({
      ...mockResult,
      status: "released",
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultReleaseScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    expect(await screen.findByText("Worklist loaded.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "result-1" }));
    expect(screen.getByRole("heading", { name: "Result detail: result-1" })).toBeInTheDocument();

    await user.type(screen.getByLabelText(/Release Notes/), "Final");
    await user.click(screen.getByRole("button", { name: "Release Result" }));

    expect(api.releaseResult).toHaveBeenCalledWith("result-1", { notes: "Final" });
    expect(await screen.findAllByText("released")).not.toHaveLength(0);
  });

  it("amends a released result", async () => {
    vi.spyOn(api, "listReleaseWorklist").mockResolvedValue([{ ...mockResult, status: "released" }]);
    vi.spyOn(api, "amendResult").mockResolvedValue({
      ...mockResult,
      status: "amended",
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultReleaseScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    await user.click(await screen.findByRole("button", { name: "result-1" }));

    await user.type(screen.getByLabelText("Amendment Reason"), "Correction");
    await user.type(screen.getByLabelText("New Raw Value"), "125");
    await user.click(screen.getByRole("button", { name: "Amend Result" }));

    expect(await screen.findByText("Result amended.")).toBeInTheDocument();
    expect(api.amendResult).toHaveBeenCalledWith(
      "result-1",
      expect.objectContaining({ reason: "Correction" }),
    );
  });
});
