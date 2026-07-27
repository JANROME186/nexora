import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AdmissionsScreen } from "../components/screens/AdmissionsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/frontDeskApi";
import type { AdmissionRequest } from "../api/types";

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

const draft: AdmissionRequest = {
  admissionId: "adm-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  visitId: "visit-1",
  patientId: "patient-1",
  consentConfirmed: false,
  sampleRequirementsAcknowledged: false,
  admissionStatus: "draft",
  version: 1,
};

describe("AdmissionsScreen", () => {
  it("starts an admission request and marks it ready for order", async () => {
    vi.spyOn(api, "startAdmissionRequest").mockResolvedValue(draft);
    vi.spyOn(api, "markAdmissionReady").mockResolvedValue({
      ...draft,
      admissionStatus: "ready_for_order",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <AdmissionsScreen />
      </ScopedHarness>,
    );

    await user.type(screen.getByLabelText("Reception visit id"), "visit-1");
    await user.type(screen.getByLabelText("Patient id"), "patient-1");
    await user.click(screen.getByRole("button", { name: "Start admission" }));

    expect(api.startAdmissionRequest).toHaveBeenCalledWith(
      expect.objectContaining({
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        branchId: "branch-1",
        visitId: "visit-1",
        patientId: "patient-1",
      }),
    );
    expect(await screen.findByText("Admission request started (draft).")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Mark ready" }));
    expect(api.markAdmissionReady).toHaveBeenCalledWith("adm-1", expect.objectContaining({}));
    expect(await screen.findByText("Admission marked ready for order.")).toBeInTheDocument();
  });

  it("commits a ready admission to a diagnostic order", async () => {
    vi.spyOn(api, "listAdmissionRequests").mockResolvedValue([
      { ...draft, admissionStatus: "ready_for_order" },
    ]);
    vi.spyOn(api, "commitAdmissionRequest").mockResolvedValue({
      ...draft,
      admissionStatus: "order_created",
      createdOrderId: "order-1",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <AdmissionsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load admissions" }));
    await user.click(await screen.findByRole("button", { name: "adm-1" }));

    await user.click(screen.getByLabelText("Consent confirmed"));
    await user.click(screen.getByLabelText("Sample requirements acknowledged"));
    await user.click(screen.getByRole("button", { name: "Commit admission" }));

    expect(api.commitAdmissionRequest).toHaveBeenCalledWith("adm-1", {
      consentConfirmed: true,
      sampleRequirementsAcknowledged: true,
    });
    expect(
      await screen.findByText("Admission committed to a diagnostic order."),
    ).toBeInTheDocument();
  });

  it("rejects a draft admission after explicit confirmation", async () => {
    vi.spyOn(api, "listAdmissionRequests").mockResolvedValue([draft]);
    vi.spyOn(api, "rejectAdmissionRequest").mockResolvedValue({
      ...draft,
      admissionStatus: "rejected",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <AdmissionsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load admissions" }));
    await user.click(await screen.findByRole("button", { name: "adm-1" }));
    await user.click(screen.getByRole("button", { name: "Reject admission" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(api.rejectAdmissionRequest).toHaveBeenCalledWith("adm-1");
    expect(await screen.findByText("Admission rejected.")).toBeInTheDocument();
  });

  it("shows an empty state when there are no admission requests", async () => {
    vi.spyOn(api, "listAdmissionRequests").mockResolvedValue([]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <AdmissionsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load admissions" }));
    expect(
      await screen.findByText("No admission requests exist yet for this tenant."),
    ).toBeInTheDocument();
  });
});
