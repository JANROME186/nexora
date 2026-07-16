import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { SalesScreen } from "../components/screens/SalesScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/cashSalesApi";
import type { PaymentAllocation, Sale, SaleLine } from "../api/types";

function ScopedSalesHarness({ children }: { children: ReactNode }) {
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

const payableSale: Sale = {
  saleId: "sale-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  patientId: "patient-1",
  sourceType: "diagnostic_order",
  sourceReferenceId: "order-1",
  totals: {
    subtotalAmount: { currency: "USD", amount: 100 },
    discountAmount: { currency: "USD", amount: 0 },
    totalAmount: { currency: "USD", amount: 100 },
    paidAmount: { currency: "USD", amount: 0 },
    outstandingAmount: { currency: "USD", amount: 100 },
  },
  status: "payable",
  version: 1,
};

const saleLine: SaleLine = {
  saleLineId: "line-1",
  saleId: "sale-1",
  catalogItemId: "test-1",
  catalogItemKind: "test",
  descriptionSnapshot: "Complete Blood Count",
  quantity: 1,
  unitAmount: { currency: "USD", amount: 100 },
  lineTotal: { currency: "USD", amount: 100 },
};

const registeredPayment: PaymentAllocation = {
  paymentId: "payment-1",
  saleId: "sale-1",
  amount: { currency: "USD", amount: 100 },
  method: "cash",
  registeredBy: "cashier-1",
};

describe("SalesScreen", () => {
  it("creates a sale from an accepted diagnostic order, registers a full payment and shows the billing hint", async () => {
    vi.spyOn(api, "createSale").mockResolvedValue(payableSale);
    vi.spyOn(api, "listSaleLines").mockResolvedValue([saleLine]);
    vi.spyOn(api, "listSalePayments")
      .mockResolvedValueOnce([])
      .mockResolvedValueOnce([registeredPayment]);
    vi.spyOn(api, "registerPayment").mockResolvedValue(registeredPayment);
    vi.spyOn(api, "getSale").mockResolvedValue({
      ...payableSale,
      status: "paid",
      version: 2,
      totals: {
        ...payableSale.totals,
        paidAmount: { currency: "USD", amount: 100 },
        outstandingAmount: { currency: "USD", amount: 0 },
      },
    });

    const user = userEvent.setup();
    render(
      <ScopedSalesHarness>
        <SalesScreen />
      </ScopedSalesHarness>,
    );

    await user.type(screen.getByLabelText("Source reference id"), "order-1");
    await user.click(screen.getByRole("button", { name: "Create sale" }));

    expect(await screen.findByText("Sale created.")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Sale detail: sale-1" })).toBeInTheDocument();
    expect(screen.getByText("Complete Blood Count")).toBeInTheDocument();
    expect(
      await screen.findByText("No payments have been registered for this sale."),
    ).toBeInTheDocument();

    await user.type(screen.getByLabelText("Amount"), "100");
    await user.type(screen.getByLabelText("Registered by (cashier id)"), "cashier-1");
    await user.click(screen.getByRole("button", { name: "Register payment" }));

    expect(await screen.findByText("Payment registered.")).toBeInTheDocument();
    expect(
      await screen.findByText(
        "This sale is fully paid. Use this Sale Id in the Billing Requests screen to create a billing request.",
      ),
    ).toBeInTheDocument();
    expect(api.registerPayment).toHaveBeenCalledWith(
      "sale-1",
      expect.objectContaining({ amount: 100, method: "cash", registeredBy: "cashier-1" }),
    );
  });

  it("surfaces the outstanding-balance conflict and supports cancelling a payable sale", async () => {
    vi.spyOn(api, "createSale").mockResolvedValue(payableSale);
    vi.spyOn(api, "listSaleLines").mockResolvedValue([saleLine]);
    vi.spyOn(api, "listSalePayments").mockResolvedValue([]);
    vi.spyOn(api, "registerPayment").mockRejectedValue(
      new ApiError(
        409,
        "PAYMENT_EXCEEDS_OUTSTANDING_BALANCE: payment cannot exceed outstanding sale balance.",
      ),
    );
    vi.spyOn(api, "cancelSale").mockResolvedValue({
      ...payableSale,
      status: "cancelled",
      cancellationReason: "patient_request",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedSalesHarness>
        <SalesScreen />
      </ScopedSalesHarness>,
    );

    await user.type(screen.getByLabelText("Source reference id"), "order-1");
    await user.click(screen.getByRole("button", { name: "Create sale" }));
    await screen.findByRole("heading", { name: "Sale detail: sale-1" });

    await user.type(screen.getByLabelText("Amount"), "500");
    await user.type(screen.getByLabelText("Registered by (cashier id)"), "cashier-1");
    await user.click(screen.getByRole("button", { name: "Register payment" }));
    expect(await screen.findByText(/PAYMENT_EXCEEDS_OUTSTANDING_BALANCE/)).toBeInTheDocument();

    await user.type(screen.getByLabelText("Reason code"), "patient_request");
    await user.click(screen.getByRole("button", { name: "Cancel sale" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(await screen.findByText("Sale cancelled.")).toBeInTheDocument();
    expect(api.cancelSale).toHaveBeenCalledWith("sale-1", { reasonCode: "patient_request" });
  });
});
