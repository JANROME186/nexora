import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InventoryReagentsScreen } from "../components/screens/InventoryReagentsScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import * as api from "../api/inventoryQualityApi";

function Harness({ children }: { children: ReactNode }) {
  return <LocaleProvider>{children}</LocaleProvider>;
}

describe("InventoryReagentsScreen", () => {
  it("assigns and loads a reagent profile", async () => {
    vi.spyOn(api, "assignReagentProfile").mockResolvedValue({
      inventoryItemId: "item-1",
      reagentCategory: "CONTROL",
      consumptionUnitRatio: "1.5",
    });
    vi.spyOn(api, "getReagentProfile").mockResolvedValue({
      inventoryItemId: "item-1",
      reagentCategory: "CONTROL",
      consumptionUnitRatio: "1.5",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <InventoryReagentsScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del artículo de inventario"), "item-1");
    await user.type(screen.getByLabelText("Categoría del reactivo"), "CONTROL");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Perfil de reactivo asignado.")).toBeInTheDocument();
    expect(api.assignReagentProfile).toHaveBeenCalledWith(
      "item-1",
      expect.objectContaining({ reagentCategory: "CONTROL" }),
    );

    await user.click(screen.getByRole("button", { name: "Cargar perfil" }));
    expect(await screen.findByText("Registros cargados.")).toBeInTheDocument();
    expect(api.getReagentProfile).toHaveBeenCalledWith("item-1");
  });
});
