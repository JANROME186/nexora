import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InventoryWasteScreen } from "../components/screens/InventoryWasteScreen";
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

describe("InventoryWasteScreen", () => {
  it("confirms before recording waste disposal and loads waste records", async () => {
    vi.spyOn(api, "applyWaste").mockResolvedValue({
      wasteRecordId: "waste-1",
      inventoryItemId: "item-1",
      stockLotId: "lot-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      reasonCode: "EXPIRED",
      disposedQuantity: "4",
    });
    vi.spyOn(api, "listWasteRecords").mockResolvedValue([]);
    const user = userEvent.setup();

    render(
      <Harness>
        <InventoryWasteScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del artículo de inventario"), "item-1");
    await user.type(screen.getByLabelText("ID del lote"), "lot-1");
    await user.type(screen.getByLabelText("Cantidad dispuesta"), "4");
    await user.type(screen.getByLabelText("Código de motivo"), "EXPIRED");
    await user.click(screen.getByRole("button", { name: "Registrar" }));

    expect(screen.queryByText("Merma registrada.")).not.toBeInTheDocument();
    expect(api.applyWaste).not.toHaveBeenCalled();

    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("Merma registrada.")).toBeInTheDocument();
    expect(api.applyWaste).toHaveBeenCalledWith(
      expect.objectContaining({
        inventoryItemId: "item-1",
        stockLotId: "lot-1",
        disposedQuantity: "4",
      }),
    );

    await user.click(screen.getByRole("button", { name: "Cargar mermas" }));
    expect(
      await screen.findByText("No hay registros para el alcance seleccionado."),
    ).toBeInTheDocument();
  });
});
