import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InventoryAdjustmentsScreen } from "../components/screens/InventoryAdjustmentsScreen";
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

describe("InventoryAdjustmentsScreen", () => {
  it("registers an adjustment and loads the adjustment list", async () => {
    vi.spyOn(api, "applyAdjustment").mockResolvedValue({
      adjustmentId: "adj-1",
      inventoryItemId: "item-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      reasonCode: "COUNT_CORRECTION",
      approverId: "user-2",
      requestedBy: "user-1",
      deltaQuantity: "-3",
    });
    vi.spyOn(api, "listAdjustments").mockResolvedValue([
      {
        adjustmentId: "adj-1",
        inventoryItemId: "item-1",
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        branchId: "branch-1",
        reasonCode: "COUNT_CORRECTION",
        approverId: "user-2",
        requestedBy: "user-1",
        deltaQuantity: "-3",
      },
    ]);
    const user = userEvent.setup();

    render(
      <Harness>
        <InventoryAdjustmentsScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del artículo de inventario"), "item-1");
    await user.type(screen.getByLabelText("Cantidad de ajuste"), "-3");
    await user.type(screen.getByLabelText("Código de motivo"), "COUNT_CORRECTION");
    await user.type(screen.getByLabelText("Solicitado por"), "user-1");
    await user.type(screen.getByLabelText("ID del aprobador"), "user-2");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Ajuste registrado.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cargar ajustes" }));
    expect(await screen.findByText("Registros cargados.")).toBeInTheDocument();
    expect(api.listAdjustments).toHaveBeenCalledWith("tenant-1", "lab-1", "branch-1");
  });
});
