import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { CriticalEscalationsScreen } from "../components/screens/CriticalEscalationsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/resultsDeliveryApi";
import type { CriticalResultEscalation } from "../api/types";

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

const mockEscalation: CriticalResultEscalation = {
  escalationId: "esc-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  resultId: "res-1",
  criticalReason: "Critical high potassium",
  escalationTier: 1,
  acknowledgementDeadline: "2099-12-31T23:59:59Z",
  status: "OPEN",
};

describe("CriticalEscalationsScreen", () => {
  it("renders critical escalations heading", () => {
    render(
      <ScopedHarness>
        <CriticalEscalationsScreen />
      </ScopedHarness>,
    );
    expect(
      screen.getByRole("heading", { name: "Critical Result Escalation Worklist" }),
    ).toBeInTheDocument();
  });

  it("loads open escalations worklist", async () => {
    vi.spyOn(api, "listOpenEscalations").mockResolvedValue([mockEscalation]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <CriticalEscalationsScreen />
      </ScopedHarness>,
    );
    await user.click(screen.getByRole("button", { name: "Load Open Escalations" }));
    expect(await screen.findByText("Escalations loaded.")).toBeInTheDocument();
    expect(screen.getByText("esc-1")).toBeInTheDocument();
    expect(screen.getByText("Critical high potassium")).toBeInTheDocument();
  });

  it("shows empty state when no escalations", async () => {
    vi.spyOn(api, "listOpenEscalations").mockResolvedValue([]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <CriticalEscalationsScreen />
      </ScopedHarness>,
    );
    await user.click(screen.getByRole("button", { name: "Load Open Escalations" }));
    expect(await screen.findByText(/No open critical escalations/)).toBeInTheDocument();
  });

  it("acknowledges an escalation", async () => {
    vi.spyOn(api, "listOpenEscalations").mockResolvedValue([mockEscalation]);
    vi.spyOn(api, "acknowledgeCriticalEscalation").mockResolvedValue({
      ...mockEscalation,
      status: "ACKNOWLEDGED",
      acknowledgedBy: "user-1",
      acknowledgedAt: "2026-07-17T12:00:00Z",
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <CriticalEscalationsScreen />
      </ScopedHarness>,
    );
    await user.click(screen.getByRole("button", { name: "Load Open Escalations" }));
    await screen.findByText("esc-1");
    await user.click(screen.getByRole("button", { name: "esc-1" }));

    await user.type(screen.getByLabelText("Acknowledging User ID"), "user-1");
    await user.click(screen.getByRole("button", { name: "Acknowledge" }));

    expect(await screen.findByText("Escalation acknowledged.")).toBeInTheDocument();
    expect(api.acknowledgeCriticalEscalation).toHaveBeenCalledWith(
      "esc-1",
      "user-1",
      "current_user",
    );
  });

  it("escalates to next tier", async () => {
    vi.spyOn(api, "listOpenEscalations").mockResolvedValue([mockEscalation]);
    vi.spyOn(api, "escalateCriticalEscalation").mockResolvedValue({
      ...mockEscalation,
      status: "ESCALATED",
      escalationTier: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <CriticalEscalationsScreen />
      </ScopedHarness>,
    );
    await user.click(screen.getByRole("button", { name: "Load Open Escalations" }));
    await screen.findByText("esc-1");
    await user.click(screen.getByRole("button", { name: "esc-1" }));
    await user.click(screen.getByRole("button", { name: "Escalate" }));

    expect(await screen.findByText("Escalation escalated to next tier.")).toBeInTheDocument();
    expect(api.escalateCriticalEscalation).toHaveBeenCalledWith("esc-1", "current_user");
  });

  it("closes an acknowledged escalation", async () => {
    const acknowledged: CriticalResultEscalation = {
      ...mockEscalation,
      status: "ACKNOWLEDGED",
      acknowledgedBy: "user-1",
      acknowledgedAt: "2026-07-17T12:00:00Z",
    };
    vi.spyOn(api, "listOpenEscalations").mockResolvedValue([acknowledged]);
    vi.spyOn(api, "closeCriticalEscalation").mockResolvedValue({
      ...acknowledged,
      status: "CLOSED",
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <CriticalEscalationsScreen />
      </ScopedHarness>,
    );
    await user.click(screen.getByRole("button", { name: "Load Open Escalations" }));
    await screen.findByText("esc-1");
    await user.click(screen.getByRole("button", { name: "esc-1" }));
    await user.click(screen.getByRole("button", { name: "Close Escalation" }));

    expect(await screen.findByText("Escalation closed.")).toBeInTheDocument();
    expect(api.closeCriticalEscalation).toHaveBeenCalledWith("esc-1", "current_user");
  });
});
