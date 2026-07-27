import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QuotationsScreen } from "../components/screens/QuotationsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/frontDeskApi";
import type { QuotationRequest } from "../api/types";

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

const draft: QuotationRequest = {
  quotationId: "quo-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  patientId: "patient-1",
  channel: "employee_portal",
  status: "draft",
  version: 1,
};

describe("QuotationsScreen", () => {
  it("starts a quotation and issues it", async () => {
    vi.spyOn(api, "startQuotation").mockResolvedValue(draft);
    vi.spyOn(api, "issueQuotation").mockResolvedValue({
      ...draft,
      status: "issued",
      totalAmount: { currency: "MXN", amount: 500 },
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <QuotationsScreen />
      </ScopedHarness>,
    );

    await user.type(screen.getByLabelText("Patient id (optional)"), "patient-1");
    await user.click(screen.getByRole("button", { name: "Start quotation" }));

    expect(api.startQuotation).toHaveBeenCalledWith(
      expect.objectContaining({
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        branchId: "branch-1",
        patientId: "patient-1",
        channel: "employee_portal",
      }),
    );
    expect(await screen.findByText("Quotation drafted.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Issue quotation" }));
    expect(api.issueQuotation).toHaveBeenCalledWith("quo-1", {});
    expect(await screen.findByText("Quotation issued.")).toBeInTheDocument();
    expect(screen.getAllByText("MXN 500.00")).toHaveLength(2);
  });

  it("accepts an issued quotation and converts it to a diagnostic order", async () => {
    vi.spyOn(api, "listQuotations").mockResolvedValue([{ ...draft, status: "issued" }]);
    vi.spyOn(api, "acceptQuotation").mockResolvedValue({
      ...draft,
      status: "accepted",
      version: 2,
    });
    vi.spyOn(api, "convertQuotation").mockResolvedValue({
      ...draft,
      status: "converted",
      convertedOrderId: "order-1",
      version: 3,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <QuotationsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load quotations" }));
    await user.click(await screen.findByRole("button", { name: "quo-1" }));

    await user.click(screen.getByRole("button", { name: "Accept quotation" }));
    expect(api.acceptQuotation).toHaveBeenCalledWith("quo-1");
    expect(await screen.findByText("Quotation accepted.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Convert to diagnostic order" }));
    expect(api.convertQuotation).toHaveBeenCalledWith("quo-1");
    expect(
      await screen.findByText("Quotation converted to a diagnostic order."),
    ).toBeInTheDocument();
  });

  it("cancels a quotation with a reason code after explicit confirmation", async () => {
    vi.spyOn(api, "listQuotations").mockResolvedValue([draft]);
    vi.spyOn(api, "cancelQuotation").mockResolvedValue({
      ...draft,
      status: "cancelled",
      cancellationReason: "no_longer_needed",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <QuotationsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load quotations" }));
    await user.click(await screen.findByRole("button", { name: "quo-1" }));
    await user.type(screen.getByLabelText("Reason code (optional)"), "no_longer_needed");
    await user.click(screen.getByRole("button", { name: "Cancel quotation" }));
    await user.click(screen.getByRole("button", { name: "Confirm" }));

    expect(api.cancelQuotation).toHaveBeenCalledWith("quo-1", {
      reasonCode: "no_longer_needed",
    });
    expect(await screen.findByText("Quotation cancelled.")).toBeInTheDocument();
  });

  it("shows an empty state when there are no quotations", async () => {
    vi.spyOn(api, "listQuotations").mockResolvedValue([]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <QuotationsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Load quotations" }));
    expect(await screen.findByText("No quotations exist yet for this tenant.")).toBeInTheDocument();
  });
});
