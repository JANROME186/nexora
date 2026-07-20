import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InternalQualityControlsScreen } from "../components/screens/InternalQualityControlsScreen";
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

const RUN = {
  qcRunId: "qc-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  testDefinitionId: "test-1",
  controlMaterialStockLotId: "lot-1",
  ruleEvaluation: "IN_CONTROL",
  acceptanceDecision: "ACCEPTED",
  performedBy: "user-1",
  measuredValue: "10",
  expectedMin: "8",
  expectedMax: "12",
  linkedLaboratoryResultIds: [],
};

describe("InternalQualityControlsScreen", () => {
  it("records a QC run, loads runs and overrides the decision for the selected run", async () => {
    vi.spyOn(api, "recordQualityControlRun").mockResolvedValue(RUN);
    vi.spyOn(api, "listQualityControlRuns").mockResolvedValue([RUN]);
    vi.spyOn(api, "overrideQualityControlDecision").mockResolvedValue({
      ...RUN,
      acceptanceDecision: "REJECTED",
      overrideBy: "user-2",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <InternalQualityControlsScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID de la prueba"), "test-1");
    await user.type(screen.getByLabelText("ID del lote de material de control"), "lot-1");
    await user.type(screen.getByLabelText("Valor medido"), "10");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Corrida de control registrada.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cargar corridas" }));
    expect(await screen.findByText("test-1")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "test-1" }));
    await user.type(screen.getByLabelText("Motivo de la anulación"), "Recalibrated instrument");
    await user.type(screen.getByLabelText("ID del supervisor"), "user-2");
    await user.click(screen.getByRole("button", { name: "Anular decisión" }));
    expect(await screen.findByText("Decisión anulada.")).toBeInTheDocument();
    expect(api.overrideQualityControlDecision).toHaveBeenCalledWith(
      "qc-1",
      expect.objectContaining({ supervisorId: "user-2" }),
    );
  });
});
