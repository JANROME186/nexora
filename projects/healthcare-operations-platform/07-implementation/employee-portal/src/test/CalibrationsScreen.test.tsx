import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { CalibrationsScreen } from "../components/screens/CalibrationsScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import * as api from "../api/inventoryQualityApi";

function Harness({ children }: { children: ReactNode }) {
  return <LocaleProvider>{children}</LocaleProvider>;
}

const CALIBRATION = {
  calibrationEventId: "cal-1",
  inventoryItemId: "item-1",
  tenantId: "tenant-1",
  branchId: "branch-1",
  calibrationStandardRef: "STD-1",
  performedBy: "user-1",
  result: "PASS",
};

describe("CalibrationsScreen", () => {
  it("records a calibration event and loads the calibration log", async () => {
    vi.spyOn(api, "recordCalibration").mockResolvedValue(CALIBRATION);
    vi.spyOn(api, "listCalibrations").mockResolvedValue([CALIBRATION]);
    const user = userEvent.setup();

    render(
      <Harness>
        <CalibrationsScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del artículo de inventario"), "item-1");
    await user.type(screen.getByLabelText("Referencia del estándar"), "STD-1");
    await user.type(screen.getByLabelText("Realizado por"), "user-1");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Calibración registrada.")).toBeInTheDocument();
    expect(api.recordCalibration).toHaveBeenCalledWith(
      "item-1",
      expect.objectContaining({ calibrationStandardRef: "STD-1" }),
    );

    await user.click(screen.getByRole("button", { name: "Cargar calibraciones" }));
    expect(await screen.findByText("STD-1")).toBeInTheDocument();
  });
});
