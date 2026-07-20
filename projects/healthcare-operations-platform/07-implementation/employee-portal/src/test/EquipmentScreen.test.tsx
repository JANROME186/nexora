import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { EquipmentScreen } from "../components/screens/EquipmentScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import * as api from "../api/inventoryQualityApi";

function Harness({ children }: { children: ReactNode }) {
  return <LocaleProvider>{children}</LocaleProvider>;
}

describe("EquipmentScreen", () => {
  it("sets the equipment profile and changes availability", async () => {
    vi.spyOn(api, "setEquipmentProfile").mockResolvedValue({
      assetTag: "EQ-1",
      availabilityStatus: "AVAILABLE",
    });
    vi.spyOn(api, "changeEquipmentAvailability").mockResolvedValue({
      assetTag: "EQ-1",
      availabilityStatus: "IN_USE",
    });
    vi.spyOn(api, "listEquipmentAvailabilityHistory").mockResolvedValue([
      {
        changeId: "change-1",
        inventoryItemId: "item-1",
        previousStatus: "AVAILABLE",
        newStatus: "IN_USE",
        reasonCode: "SCHEDULED_TEST_RUN",
        changedBy: "current_user",
      },
    ]);
    const user = userEvent.setup();

    render(
      <Harness>
        <EquipmentScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del artículo de inventario"), "item-1");
    await user.type(screen.getByLabelText("Etiqueta de activo"), "EQ-1");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Perfil de equipo registrado.")).toBeInTheDocument();

    await user.type(screen.getByLabelText("Código de motivo"), "SCHEDULED_TEST_RUN");
    await user.click(screen.getByRole("button", { name: "Cambiar disponibilidad" }));
    expect(await screen.findByText("Disponibilidad actualizada.")).toBeInTheDocument();
    expect(api.changeEquipmentAvailability).toHaveBeenCalledWith(
      "item-1",
      expect.objectContaining({ newStatus: "IN_USE", reasonCode: "SCHEDULED_TEST_RUN" }),
    );

    await user.click(screen.getByRole("button", { name: "Cargar historial" }));
    expect(await screen.findByText("SCHEDULED_TEST_RUN")).toBeInTheDocument();
  });
});
