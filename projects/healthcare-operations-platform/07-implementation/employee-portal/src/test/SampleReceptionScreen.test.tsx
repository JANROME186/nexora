import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { SampleReceptionScreen } from "../components/screens/SampleReceptionScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/laboratoryOperationsApi";
import type { Sample } from "../api/types";

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

const mockSample: Sample = {
  sampleId: "sample-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  orderId: "order-1",
  patientId: "patient-1",
  sampleTypeRefId: "blood",
  status: "pending_reception",
  version: 1,
};

describe("SampleReceptionScreen", () => {
  it("loads worklist and receives a sample", async () => {
    vi.spyOn(api, "listReceptionWorklist").mockResolvedValue([mockSample]);
    vi.spyOn(api, "receiveSample").mockResolvedValue({
      ...mockSample,
      status: "received",
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <SampleReceptionScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    expect(await screen.findByText("Worklist loaded.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "sample-1" }));
    expect(screen.getByRole("heading", { name: "Sample detail: sample-1" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Receive Sample" }));

    expect(api.receiveSample).toHaveBeenCalledWith("sample-1", { conditionCriteriaMet: true });
    expect(await screen.findAllByText("received")).not.toHaveLength(0);
  });

  it("surfaces an error when rejecting a sample without reason", async () => {
    vi.spyOn(api, "listReceptionWorklist").mockResolvedValue([mockSample]);
    vi.spyOn(api, "rejectSample").mockRejectedValue(new ApiError(400, "MISSING_REASON"));

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <SampleReceptionScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    await user.click(await screen.findByRole("button", { name: "sample-1" }));

    // Type reason since it's required by HTML5, but we mock the API to fail anyway
    await user.type(screen.getAllByLabelText(/Reason Code/)[0], "DAMAGED");
    await user.click(screen.getByRole("button", { name: "Reject Sample" }));
    expect(await screen.findByText(/MISSING_REASON/)).toBeInTheDocument();
  });
});
