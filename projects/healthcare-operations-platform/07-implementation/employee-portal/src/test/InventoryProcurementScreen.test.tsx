import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InventoryProcurementScreen } from "../components/screens/InventoryProcurementScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/inventoryQualityApi";

function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>
        <ScopeSetter />
        {children}
      </AdminScopeProvider>
    </LocaleProvider>
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
  }, [setTenantId, setLaboratoryId, setBranchId]);
  return null;
}

const ORDER = {
  purchaseOrderId: "po-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  supplierId: "sup-1",
  supplierName: "Acme",
  status: "DRAFT",
  currencyCode: "MXN",
  totalAmount: "500",
  lines: [],
};

describe("InventoryProcurementScreen", () => {
  it("adds a line, creates a purchase order, loads orders and submits the selected order", async () => {
    vi.spyOn(api, "createPurchaseOrder").mockResolvedValue(ORDER);
    vi.spyOn(api, "listPurchaseOrders").mockResolvedValue([ORDER]);
    vi.spyOn(api, "submitPurchaseOrder").mockResolvedValue({ ...ORDER, status: "SUBMITTED" });
    const user = userEvent.setup();

    render(
      <Harness>
        <InventoryProcurementScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del proveedor"), "sup-1");
    await user.type(screen.getByLabelText("Nombre del proveedor"), "Acme");
    await user.type(screen.getByLabelText("ID del artículo (línea)"), "item-1");
    await user.type(screen.getByLabelText("Cantidad ordenada"), "10");
    await user.type(screen.getByLabelText("Costo unitario"), "5");
    await user.click(screen.getByRole("button", { name: "Agregar línea" }));
    expect(screen.getByText("item-1: 10 x 5")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Orden de compra creada.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cargar órdenes" }));
    expect(await screen.findByText("po-1")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "po-1" }));
    await user.click(screen.getByRole("button", { name: "Enviar" }));
    expect(await screen.findByText("Orden enviada.")).toBeInTheDocument();
    expect(api.submitPurchaseOrder).toHaveBeenCalledWith("po-1", "current_user");
  });
});
