import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InventoryLotsScreen } from "../components/screens/InventoryLotsScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import * as api from "../api/inventoryQualityApi";

function Harness({ children }: { children: ReactNode }) {
  return <LocaleProvider>{children}</LocaleProvider>;
}

const LOT = {
  stockLotId: "lot-1",
  inventoryItemId: "item-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  lotNumber: "LOT-1",
  supplierName: "Acme",
  status: "ACTIVE",
  receivedQuantity: "100",
  remainingQuantity: "100",
};

describe("InventoryLotsScreen", () => {
  it("registers a lot, loads lots and quarantines the selected lot", async () => {
    vi.spyOn(api, "registerStockLot").mockResolvedValue(LOT);
    vi.spyOn(api, "listStockLots").mockResolvedValue([LOT]);
    vi.spyOn(api, "quarantineStockLot").mockResolvedValue({ ...LOT, status: "QUARANTINED" });
    const user = userEvent.setup();

    render(
      <Harness>
        <InventoryLotsScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del artículo de inventario"), "item-1");
    await user.type(screen.getByLabelText("Número de lote"), "LOT-1");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Lote registrado.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cargar lotes" }));
    expect(await screen.findByText("LOT-1")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "LOT-1" }));
    await user.click(screen.getByRole("button", { name: "Poner en cuarentena" }));
    expect(await screen.findByText("Lote puesto en cuarentena.")).toBeInTheDocument();
    expect(api.quarantineStockLot).toHaveBeenCalledWith("lot-1", "current_user");
  });
});
