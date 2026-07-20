import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MaintenanceScreen } from "../components/screens/MaintenanceScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import * as api from "../api/inventoryQualityApi";

function Harness({ children }: { children: ReactNode }) {
  return <LocaleProvider>{children}</LocaleProvider>;
}

const EVENT = {
  maintenanceEventId: "maint-1",
  inventoryItemId: "item-1",
  tenantId: "tenant-1",
  branchId: "branch-1",
  maintenanceType: "PREVENTIVE",
  description: "Quarterly service",
};

describe("MaintenanceScreen", () => {
  it("records a maintenance event, loads events and completes the selected event", async () => {
    vi.spyOn(api, "recordMaintenance").mockResolvedValue(EVENT);
    vi.spyOn(api, "listMaintenanceEvents").mockResolvedValue([EVENT]);
    vi.spyOn(api, "completeMaintenance").mockResolvedValue({
      ...EVENT,
      completedAt: "2026-07-20T00:00:00Z",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <MaintenanceScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del artículo de inventario"), "item-1");
    await user.type(screen.getByLabelText("Descripción"), "Quarterly service");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Mantenimiento registrado.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cargar eventos" }));
    expect(await screen.findByText("Quarterly service")).toBeInTheDocument();

    await user.type(screen.getByLabelText("ID del evento de mantenimiento"), "maint-1");
    await user.click(screen.getByRole("button", { name: "Completar" }));
    expect(await screen.findByText("Mantenimiento completado.")).toBeInTheDocument();
    expect(api.completeMaintenance).toHaveBeenCalledWith("maint-1", { actorId: "current_user" });
  });
});
