import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { DiagnosticOrdersScreen } from "../components/screens/DiagnosticOrdersScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/frontDeskApi";
import type { DiagnosticOrder } from "../api/types";

function ScopedOrdersHarness({ children }: { children: ReactNode }) {
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

const baseOrder: DiagnosticOrder = {
  orderId: "order-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  intakeChannel: "walk_in",
  patientSnapshot: {
    patientId: "patient-1",
    sourceVersion: 1,
    fullName: "Rosalind Franklin",
    documentType: "national_id",
    documentNumberMasked: "***1234",
    capturedAt: "2026-07-16T00:00:00Z",
  },
  branchSnapshot: {
    branchId: "branch-1",
    sourceVersion: 1,
    name: "Main Branch",
    capturedAt: "2026-07-16T00:00:00Z",
  },
  status: "draft",
  version: 1,
};

describe("DiagnosticOrdersScreen", () => {
  it("creates a walk-in order and prices it", async () => {
    vi.spyOn(api, "createDiagnosticOrder").mockResolvedValue(baseOrder);
    vi.spyOn(api, "priceDiagnosticOrder").mockResolvedValue({
      ...baseOrder,
      status: "priced",
      pricingSnapshot: {
        priceListId: "price-list-1",
        priceListVersion: 1,
        totalAmount: { currency: "USD", amount: 120 },
        capturedAt: "2026-07-16T00:00:00Z",
      },
    });

    const user = userEvent.setup();
    render(
      <ScopedOrdersHarness>
        <DiagnosticOrdersScreen />
      </ScopedOrdersHarness>,
    );

    await user.type(screen.getByLabelText("Patient id"), "patient-1");
    await user.type(screen.getByLabelText("Catalog item id"), "test-1");
    await user.click(screen.getByRole("button", { name: "Create order" }));

    expect(await screen.findByText("Diagnostic order created (draft).")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Order detail: order-1" })).toBeInTheDocument();
    expect(screen.getByText("Rosalind Franklin (national_id ***1234)")).toBeInTheDocument();
    expect(api.createDiagnosticOrder).toHaveBeenCalledWith(
      expect.objectContaining({
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        branchId: "branch-1",
        intakeChannel: "walk_in",
        patientId: "patient-1",
        lines: [{ testDefinitionId: "test-1", catalogItemKind: "test", quantity: 1 }],
      }),
    );

    await user.click(screen.getByRole("button", { name: "Price order" }));
    expect(await screen.findByText("Order priced.")).toBeInTheDocument();
    expect(screen.getAllByText(/USD 120\.00/).length).toBeGreaterThan(0);
  });

  it("surfaces the override-justification business conflict when cancelling a clinically engaged order", async () => {
    const acceptedOrder: DiagnosticOrder = { ...baseOrder, status: "accepted" };
    vi.spyOn(api, "createDiagnosticOrder").mockResolvedValue(acceptedOrder);
    vi.spyOn(api, "cancelDiagnosticOrder").mockRejectedValue(
      new ApiError(
        409,
        "ORDER_CANCELLATION_OVERRIDE_REQUIRED: cancelling an accepted or in-progress order requires " +
          "an override justification of at least 15 characters.",
      ),
    );

    const user = userEvent.setup();
    render(
      <ScopedOrdersHarness>
        <DiagnosticOrdersScreen />
      </ScopedOrdersHarness>,
    );

    await user.type(screen.getByLabelText("Patient id"), "patient-1");
    await user.type(screen.getByLabelText("Catalog item id"), "test-1");
    await user.click(screen.getByRole("button", { name: "Create order" }));
    await screen.findByRole("heading", { name: "Order detail: order-1" });

    await user.type(screen.getByLabelText("Reason code"), "patient_request");
    await user.click(screen.getByRole("button", { name: "Cancel order" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(await screen.findByText(/ORDER_CANCELLATION_OVERRIDE_REQUIRED/)).toBeInTheDocument();
  });
});
