import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ResultSearchScreen } from "../components/screens/ResultSearchScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/resultsDeliveryApi";
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
  status: "released",
  resultValues: [{ rawValue: "5.4", capturedAt: "2026-07-17T00:00:00Z", capturedBy: "analyst" }],
  incidents: [],
  analyteSnapshots: [],
  referenceRangeSnapshots: [],
  amendments: [],
  releaseRecord: { releasedBy: "doc-1", releasedAt: "2026-07-17T00:03:00Z", notes: "ok" },
  version: 2,
};

const mockCriticalResult: LaboratoryResult = {
  ...mockResult,
  resultId: "result-critical",
  referenceRangeSnapshots: [{ rangeRefId: "rr-1", criticalHigh: 10.0 }],
};

describe("ResultSearchScreen", () => {
  it("loads released results worklist", async () => {
    vi.spyOn(api, "listReleasedResults").mockResolvedValue([mockResult]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultSearchScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Released Results" }));
    expect(await screen.findByText("Results loaded.")).toBeInTheDocument();
    expect(screen.getByText("result-1")).toBeInTheDocument();
    expect(api.listReleasedResults).toHaveBeenCalledWith("tenant-1");
  });

  it("shows empty state when no results", async () => {
    vi.spyOn(api, "listReleasedResults").mockResolvedValue([]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultSearchScreen />
      </ScopedHarness>,
    );
    await user.click(screen.getByRole("button", { name: "Load Released Results" }));
    expect(await screen.findByText(/No released results/)).toBeInTheDocument();
  });

  it("shows result detail when a result is selected", async () => {
    vi.spyOn(api, "listReleasedResults").mockResolvedValue([mockResult]);
    vi.spyOn(api, "getResultById").mockResolvedValue(mockResult);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultSearchScreen />
      </ScopedHarness>,
    );
    await user.click(screen.getByRole("button", { name: "Load Released Results" }));
    await screen.findByText("result-1");
    await user.click(screen.getByRole("button", { name: "result-1" }));

    expect(await screen.findByText(/Result Detail:/)).toBeInTheDocument();
    expect(screen.getAllByText("test-def-1").length).toBeGreaterThanOrEqual(1);
  });

  it("shows critical indicator for results with critical reference ranges", async () => {
    vi.spyOn(api, "listReleasedResults").mockResolvedValue([mockCriticalResult]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultSearchScreen />
      </ScopedHarness>,
    );
    await user.click(screen.getByRole("button", { name: "Load Released Results" }));
    const criticalLabel = await screen.findByLabelText("Critical indicator");
    expect(criticalLabel).toBeInTheDocument();
  });

  it("shows error guard when no tenant selected", () => {
    render(
      <AdminScopeProvider>
        <ResultSearchScreen />
      </AdminScopeProvider>,
    );
    expect(screen.getByText(/Select a tenant/)).toBeInTheDocument();
  });
});
