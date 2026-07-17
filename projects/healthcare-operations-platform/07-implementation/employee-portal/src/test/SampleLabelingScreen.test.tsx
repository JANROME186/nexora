import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { SampleLabelingScreen } from "../components/screens/SampleLabelingScreen";
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

describe("SampleLabelingScreen", () => {
  it("loads a sample and requests a print job", async () => {
    vi.spyOn(api, "getSample").mockResolvedValue(mockSample);
    vi.spyOn(api, "getLabelPrintJob").mockResolvedValue({ jobId: "job-1", status: "queued" });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <SampleLabelingScreen />
      </ScopedHarness>,
    );

    await user.type(screen.getByLabelText("Sample Id"), "sample-1");
    await user.click(screen.getByRole("button", { name: "Load Sample" }));
    expect(await screen.findByText("Sample loaded.")).toBeInTheDocument();

    expect(screen.getByRole("heading", { name: "Labeling: sample-1" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Print Label" }));

    expect(await screen.findByText("Label print job queued.")).toBeInTheDocument();
    expect(await screen.findByText(/job-1/)).toBeInTheDocument();
    expect(api.getLabelPrintJob).toHaveBeenCalledWith("sample-1");
  });

  it("surfaces a business conflict when loading fails", async () => {
    vi.spyOn(api, "getSample").mockRejectedValue(new ApiError(404, "SAMPLE_NOT_FOUND"));

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <SampleLabelingScreen />
      </ScopedHarness>,
    );

    await user.type(screen.getByLabelText("Sample Id"), "sample-999");
    await user.click(screen.getByRole("button", { name: "Load Sample" }));

    expect(await screen.findByText(/SAMPLE_NOT_FOUND/)).toBeInTheDocument();
  });
});
