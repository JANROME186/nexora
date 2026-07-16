import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ReceptionScreen } from "../components/screens/ReceptionScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/frontDeskApi";
import type { ReceptionVisit } from "../api/types";

function ScopedReceptionHarness({ children }: { children: ReactNode }) {
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

const startedVisit: ReceptionVisit = {
  visitId: "visit-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  patientId: "patient-1",
  intakeChannel: "walk_in",
  identityConfirmed: false,
  queueStatus: "waiting",
  priority: "normal",
  version: 1,
};

describe("ReceptionScreen", () => {
  it("starts a walk-in visit and surfaces a business conflict when advancing before identity is confirmed", async () => {
    vi.spyOn(api, "startReceptionVisit").mockResolvedValue(startedVisit);
    vi.spyOn(api, "advanceReceptionToAdmission").mockRejectedValue(
      new ApiError(
        409,
        "RECEPTION_IDENTITY_NOT_CONFIRMED: identity must be confirmed before advancing to admission.",
      ),
    );

    const user = userEvent.setup();
    render(
      <ScopedReceptionHarness>
        <ReceptionScreen />
      </ScopedReceptionHarness>,
    );

    await user.type(screen.getByLabelText("Patient id"), "patient-1");
    await user.click(screen.getByRole("button", { name: "Start visit" }));

    expect(await screen.findByText("Reception visit started.")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Visit detail: visit-1" })).toBeInTheDocument();
    expect(api.startReceptionVisit).toHaveBeenCalledWith(
      expect.objectContaining({
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        branchId: "branch-1",
        patientId: "patient-1",
        intakeChannel: "walk_in",
      }),
    );

    await user.click(screen.getByRole("button", { name: "Advance to admission" }));
    expect(await screen.findByText(/RECEPTION_IDENTITY_NOT_CONFIRMED/)).toBeInTheDocument();
  });

  it("confirms identity for the selected visit", async () => {
    vi.spyOn(api, "startReceptionVisit").mockResolvedValue(startedVisit);
    vi.spyOn(api, "confirmReceptionIdentity").mockResolvedValue({
      ...startedVisit,
      identityConfirmed: true,
      identityConfirmationMethod: "document_check",
    });

    const user = userEvent.setup();
    render(
      <ScopedReceptionHarness>
        <ReceptionScreen />
      </ScopedReceptionHarness>,
    );

    await user.type(screen.getByLabelText("Patient id"), "patient-1");
    await user.click(screen.getByRole("button", { name: "Start visit" }));
    await screen.findByText("Reception visit started.");

    await user.click(screen.getByRole("button", { name: "Confirm identity" }));

    expect(await screen.findByText("Identity confirmed.")).toBeInTheDocument();
    expect(api.confirmReceptionIdentity).toHaveBeenCalledWith(
      "visit-1",
      expect.objectContaining({ identityConfirmationMethod: "document_check" }),
    );
  });
});
