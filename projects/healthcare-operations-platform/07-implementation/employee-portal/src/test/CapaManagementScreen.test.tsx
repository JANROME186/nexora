import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { CapaManagementScreen } from "../components/screens/CapaManagementScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/externalQualityComplianceApi";

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
  const { setTenantId, setLaboratoryId } = useAdminScope();
  const initialized = useRef(false);
  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;
    setTenantId("tenant-1");
    setLaboratoryId("lab-1");
  }, [setTenantId, setLaboratoryId]);
  return null;
}

const CAPA = {
  capaId: "capa-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  sourceEventType: "contamination",
  description: "Cross-contamination incident",
  status: "open",
  findings: [],
  version: 1,
};

describe("CapaManagementScreen", () => {
  it("opens a CAPA, loads CAPAs and closes selected CAPA with confirm dialog", async () => {
    vi.spyOn(api, "openCapa").mockResolvedValue(CAPA);
    vi.spyOn(api, "listCapaRecords").mockResolvedValue([CAPA]);
    vi.spyOn(api, "closeCapa").mockResolvedValue({ ...CAPA, status: "closed" });
    const user = userEvent.setup();

    render(
      <Harness>
        <CapaManagementScreen />
      </Harness>,
    );

    // Open CAPA
    await user.type(screen.getByLabelText("Tipo de evento origen"), "contamination");
    await user.type(screen.getByLabelText("Descripción"), "Cross-contamination incident");
    await user.click(screen.getByRole("button", { name: "Abrir CAPA" }));
    expect(await screen.findByText("CAPA abierta.")).toBeInTheDocument();

    // Load CAPAs
    await user.click(screen.getByRole("button", { name: "Cargar CAPAs" }));
    expect(await screen.findByText("contamination")).toBeInTheDocument();

    // Select and close with confirm dialog
    await user.click(screen.getByRole("button", { name: "contamination" }));
    await user.type(screen.getByLabelText("Cerrado por"), "manager-1");
    await user.type(screen.getByLabelText("Acción correctiva"), "Updated SOP");
    await user.click(screen.getByRole("button", { name: "Cerrar" }));

    expect(await screen.findByRole("dialog")).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("CAPA cerrada.")).toBeInTheDocument();
    expect(api.closeCapa).toHaveBeenCalledWith(
      "capa-1",
      expect.objectContaining({ closedBy: "manager-1" }),
    );
  });

  it("shows empty state when no CAPAs loaded after load button click", async () => {
    vi.spyOn(api, "listCapaRecords").mockResolvedValue([]);
    const user = userEvent.setup();
    render(
      <Harness>
        <CapaManagementScreen />
      </Harness>,
    );
    await user.click(screen.getByRole("button", { name: "Cargar CAPAs" }));
    expect(
      await screen.findByText("No hay registros para el alcance seleccionado."),
    ).toBeInTheDocument();
  });
});
