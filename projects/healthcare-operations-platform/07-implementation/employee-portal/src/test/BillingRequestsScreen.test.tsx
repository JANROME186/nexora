import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { BillingRequestsScreen } from "../components/screens/BillingRequestsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/cashSalesApi";
import type { InvoiceRequest, TaxLine } from "../api/types";

function ScopedBillingHarness({ children }: { children: ReactNode }) {
  return (
    <AdminScopeProvider>
      <ScopeSetter />
      {children}
    </AdminScopeProvider>
  );
}

function ScopeSetter() {
  const { setTenantId } = useAdminScope();
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) {
      return;
    }
    initialized.current = true;
    setTenantId("tenant-1");
  }, [setTenantId]);

  return null;
}

const requestedInvoice: InvoiceRequest = {
  invoiceRequestId: "invoice-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  saleId: "sale-1",
  patientId: "patient-1",
  fiscalProfileSnapshot: {
    legalName: "Acme Diagnostics",
    taxIdentifier: "TAX-1",
    fiscalAddress: "123 Main St",
    capturedAt: "2026-07-16T00:00:00Z",
  },
  status: "requested",
  version: 1,
};

const taxLine: TaxLine = {
  taxLineId: "tax-line-1",
  invoiceRequestId: "invoice-1",
  baseAmount: { currency: "USD", amount: 100 },
  taxCode: "STANDARD",
  taxRate: 10,
  taxAmount: { currency: "USD", amount: 10 },
};

async function fillCreateForm(user: ReturnType<typeof userEvent.setup>) {
  await user.type(screen.getByLabelText("Sale id"), "sale-1");
  await user.type(screen.getByLabelText("Fiscal legal name"), "Acme Diagnostics");
  await user.type(screen.getByLabelText("Tax identifier"), "TAX-1");
  await user.type(screen.getByLabelText("Fiscal address"), "123 Main St");
  await user.click(screen.getByRole("button", { name: "Create billing request" }));
}

describe("BillingRequestsScreen", () => {
  it("creates a billing request, submits it, retries an adapter failure and reaches issued status", async () => {
    vi.spyOn(api, "createBillingRequest").mockResolvedValue(requestedInvoice);
    vi.spyOn(api, "listTaxLines").mockResolvedValue([taxLine]);
    vi.spyOn(api, "submitBillingRequest").mockResolvedValue({
      ...requestedInvoice,
      status: "failed",
      adapterResponseSnapshot: '{"errorCode":"BILLING_ADAPTER_TRANSIENT_ERROR","retryable":true}',
      version: 2,
    });
    vi.spyOn(api, "retryBillingRequest").mockResolvedValue({
      ...requestedInvoice,
      status: "issued",
      adapterCorrelationId: "corr-1",
      version: 3,
    });

    const user = userEvent.setup();
    render(
      <ScopedBillingHarness>
        <BillingRequestsScreen />
      </ScopedBillingHarness>,
    );

    await fillCreateForm(user);

    expect(await screen.findByText("Billing request created.")).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: "Billing request detail: invoice-1" }),
    ).toBeInTheDocument();
    expect(await screen.findByText("STANDARD")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Submit" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));
    expect(await screen.findByText("Billing request submitted.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Retry" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));
    expect(await screen.findByText("Billing request retried.")).toBeInTheDocument();
    expect(api.retryBillingRequest).toHaveBeenCalledWith("invoice-1");
    expect(screen.queryByRole("button", { name: "Submit" })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Cancel" })).not.toBeInTheDocument();
  });

  it("surfaces the paid-sale requirement conflict when creating a billing request for an unpaid sale", async () => {
    vi.spyOn(api, "createBillingRequest").mockRejectedValue(
      new ApiError(409, "BILLING_SALE_REQUIRED: billing request requires a paid sale."),
    );

    const user = userEvent.setup();
    render(
      <ScopedBillingHarness>
        <BillingRequestsScreen />
      </ScopedBillingHarness>,
    );

    await fillCreateForm(user);

    expect(await screen.findByText(/BILLING_SALE_REQUIRED/)).toBeInTheDocument();
  });
});
