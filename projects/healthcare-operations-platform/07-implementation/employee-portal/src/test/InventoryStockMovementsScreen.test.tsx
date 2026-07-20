import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InventoryStockMovementsScreen } from "../components/screens/InventoryStockMovementsScreen";
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

describe("InventoryStockMovementsScreen", () => {
  it("records a stock entry, exit and consumption record", async () => {
    vi.spyOn(api, "applyStockReceipt").mockResolvedValue({
      stockEntryId: "entry-1",
      inventoryItemId: "item-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      entryType: "PURCHASE_RECEIPT",
      quantity: "10",
    });
    vi.spyOn(api, "applyStockExit").mockResolvedValue({
      stockExitId: "exit-1",
      inventoryItemId: "item-1",
      stockLotId: "lot-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      exitType: "BRANCH_TRANSFER",
      quantity: "5",
    });
    vi.spyOn(api, "applyConsumption").mockResolvedValue({
      consumptionRecordId: "cons-1",
      inventoryItemId: "item-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      consumptionContext: "DIAGNOSTIC_TESTING",
      consumedQuantity: "2",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <InventoryStockMovementsScreen />
      </Harness>,
    );

    const itemIdInputs = screen.getAllByLabelText("ID del artículo de inventario");
    await user.type(itemIdInputs[0], "item-1");
    const quantityInputs = screen.getAllByLabelText("Cantidad");
    await user.type(quantityInputs[0], "10");
    const registerButtons = screen.getAllByRole("button", { name: "Registrar" });
    await user.click(registerButtons[0]);
    expect(await screen.findByText("Entrada registrada.")).toBeInTheDocument();
    expect(api.applyStockReceipt).toHaveBeenCalledWith(
      expect.objectContaining({ inventoryItemId: "item-1", quantity: "10" }),
    );

    await user.type(itemIdInputs[1], "item-1");
    await user.type(screen.getByLabelText("ID del lote"), "lot-1");
    await user.type(quantityInputs[1], "5");
    await user.click(registerButtons[1]);
    expect(await screen.findByText("Salida registrada.")).toBeInTheDocument();

    await user.type(itemIdInputs[2], "item-1");
    await user.type(quantityInputs[2], "2");
    await user.click(registerButtons[2]);
    expect(await screen.findByText("Consumo registrado.")).toBeInTheDocument();
  });
});
