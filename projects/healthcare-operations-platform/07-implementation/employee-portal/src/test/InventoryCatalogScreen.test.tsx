import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InventoryCatalogScreen } from "../components/screens/InventoryCatalogScreen";
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

describe("InventoryCatalogScreen", () => {
  it("registers an item, loads the catalog and discontinues the selected item", async () => {
    vi.spyOn(api, "registerInventoryItem").mockResolvedValue({
      inventoryItemId: "item-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      itemCode: "REAG-001",
      itemName: "Reagent A",
      itemType: "REAGENT",
      classification: "CONSUMABLE",
      unitOfMeasure: "ML",
      status: "ACTIVE",
    });
    vi.spyOn(api, "listInventoryItems").mockResolvedValue([
      {
        inventoryItemId: "item-1",
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        branchId: "branch-1",
        itemCode: "REAG-001",
        itemName: "Reagent A",
        itemType: "REAGENT",
        classification: "CONSUMABLE",
        unitOfMeasure: "ML",
        status: "ACTIVE",
      },
    ]);
    vi.spyOn(api, "discontinueInventoryItem").mockResolvedValue({
      inventoryItemId: "item-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      itemCode: "REAG-001",
      itemName: "Reagent A",
      itemType: "REAGENT",
      classification: "CONSUMABLE",
      unitOfMeasure: "ML",
      status: "DISCONTINUED",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <InventoryCatalogScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("Código del artículo"), "REAG-001");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Artículo registrado.")).toBeInTheDocument();
    expect(api.registerInventoryItem).toHaveBeenCalledWith(
      expect.objectContaining({ itemCode: "REAG-001", tenantId: "tenant-1", branchId: "branch-1" }),
    );

    await user.click(screen.getByRole("button", { name: "Cargar artículos" }));
    expect(await screen.findByText("REAG-001")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "REAG-001" }));
    await user.click(screen.getByRole("button", { name: "Descontinuar" }));
    expect(await screen.findByText("Artículo descontinuado.")).toBeInTheDocument();
    expect(api.discontinueInventoryItem).toHaveBeenCalledWith("item-1", "current_user");
  });
});
