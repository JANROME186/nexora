import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { SampleCollectionScreen } from "../components/screens/SampleCollectionScreen";
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
  status: "pending_collection",
  version: 1,
};

describe("SampleCollectionScreen", () => {
  it("loads worklist and collects a sample", async () => {
    vi.spyOn(api, "listCollectionWorklist").mockResolvedValue([mockSample]);
    vi.spyOn(api, "collectSample").mockResolvedValue({
      ...mockSample,
      status: "collected",
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <SampleCollectionScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    expect(await screen.findByText("Worklist loaded.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "sample-1" }));
    expect(screen.getByRole("heading", { name: "Sample detail: sample-1" })).toBeInTheDocument();

    await user.type(screen.getByLabelText(/Collected by/), "Nurse Alice");
    await user.click(screen.getByRole("button", { name: "Mark as Collected" }));

    expect(await screen.findByText("Sample collected.")).toBeInTheDocument();
    expect(api.collectSample).toHaveBeenCalledWith("sample-1", { collectedBy: "Nurse Alice" });
  });

  it("surfaces a business conflict when collection fails", async () => {
    vi.spyOn(api, "listCollectionWorklist").mockResolvedValue([mockSample]);
    vi.spyOn(api, "collectSample").mockRejectedValue(new ApiError(409, "ALREADY_COLLECTED"));

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <SampleCollectionScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load Worklist" }));
    await user.click(await screen.findByRole("button", { name: "sample-1" }));

    await user.click(screen.getByRole("button", { name: "Mark as Collected" }));
    expect(await screen.findByText(/ALREADY_COLLECTED/)).toBeInTheDocument();
  });
});
