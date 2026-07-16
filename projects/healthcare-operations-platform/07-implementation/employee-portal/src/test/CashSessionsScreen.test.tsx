import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { CashSessionsScreen } from "../components/screens/CashSessionsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/cashSalesApi";
import type { CashSession } from "../api/types";

function ScopedCashSessionsHarness({ children }: { children: ReactNode }) {
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

const baseSession: CashSession = {
  sessionId: "session-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  openedBy: "cashier-1",
  openingAmount: { currency: "USD", amount: 100 },
  expectedAmount: { currency: "USD", amount: 100 },
  status: "open",
  openedAt: "2026-07-16T00:00:00Z",
};

describe("CashSessionsScreen", () => {
  it("opens a cash session and closes it with a matching counted amount", async () => {
    vi.spyOn(api, "openCashSession").mockResolvedValue(baseSession);
    vi.spyOn(api, "closeCashSession").mockResolvedValue({
      ...baseSession,
      status: "closed",
      countedAmount: { currency: "USD", amount: 100 },
      varianceAmount: { currency: "USD", amount: 0 },
      closedAt: "2026-07-16T08:00:00Z",
    });

    const user = userEvent.setup();
    render(
      <ScopedCashSessionsHarness>
        <CashSessionsScreen />
      </ScopedCashSessionsHarness>,
    );

    await user.type(screen.getByLabelText("Opened by (cashier id)"), "cashier-1");
    await user.type(screen.getByLabelText("Opening amount"), "100");
    await user.click(screen.getByRole("button", { name: "Open session" }));

    expect(await screen.findByText("Cash session opened.")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Session detail: session-1" })).toBeInTheDocument();
    expect(screen.getAllByText("USD 100.00").length).toBeGreaterThan(0);
    expect(api.openCashSession).toHaveBeenCalledWith(
      expect.objectContaining({
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        branchId: "branch-1",
        openedBy: "cashier-1",
        openingAmount: 100,
      }),
    );

    await user.type(screen.getByLabelText("Counted amount"), "100");
    await user.click(screen.getByRole("button", { name: "Close session" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(await screen.findByText("Cash session closed.")).toBeInTheDocument();
    expect(api.closeCashSession).toHaveBeenCalledWith(
      "session-1",
      expect.objectContaining({ countedAmount: 100, varianceReason: undefined }),
    );
  });

  it("surfaces the variance-reason business conflict when counted amount differs without a reason", async () => {
    vi.spyOn(api, "openCashSession").mockResolvedValue(baseSession);
    vi.spyOn(api, "closeCashSession").mockRejectedValue(
      new ApiError(
        409,
        "CASH_VARIANCE_REASON_REQUIRED: variance reason is required when counted cash differs from expected cash.",
      ),
    );

    const user = userEvent.setup();
    render(
      <ScopedCashSessionsHarness>
        <CashSessionsScreen />
      </ScopedCashSessionsHarness>,
    );

    await user.type(screen.getByLabelText("Opened by (cashier id)"), "cashier-1");
    await user.type(screen.getByLabelText("Opening amount"), "100");
    await user.click(screen.getByRole("button", { name: "Open session" }));
    await screen.findByRole("heading", { name: "Session detail: session-1" });

    await user.type(screen.getByLabelText("Counted amount"), "80");
    await user.click(screen.getByRole("button", { name: "Close session" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(await screen.findByText(/CASH_VARIANCE_REASON_REQUIRED/)).toBeInTheDocument();
  });
});
