import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ExternalQualityControlsScreen } from "../components/screens/ExternalQualityControlsScreen";
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

const CONTROL = {
  externalQCId: "eqc-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  controlType: "ISO17025",
  providerName: "Acumed Lab",
  referenceCode: "REF-001",
  description: "External panel check",
  status: "pending",
  version: 1,
};

describe("ExternalQualityControlsScreen", () => {
  it("creates a control, loads controls and approves selected control with confirm dialog", async () => {
    vi.spyOn(api, "createExternalQualityControl").mockResolvedValue(CONTROL);
    vi.spyOn(api, "listExternalQualityControls").mockResolvedValue([CONTROL]);
    vi.spyOn(api, "approveExternalQualityControl").mockResolvedValue({
      ...CONTROL,
      status: "approved",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <ExternalQualityControlsScreen />
      </Harness>,
    );

    // Create a new external QC
    await user.type(screen.getByLabelText("Tipo de control"), "ISO17025");
    await user.type(screen.getByLabelText("Nombre del proveedor"), "Acumed Lab");
    await user.type(screen.getByLabelText("Código de referencia"), "REF-001");
    await user.type(screen.getByLabelText("Descripción"), "External panel check");
    await user.click(screen.getByRole("button", { name: "Registrar" }));
    expect(await screen.findByText("Control registrado.")).toBeInTheDocument();

    // Load controls
    await user.click(screen.getByRole("button", { name: "Cargar controles" }));
    expect(await screen.findByText("REF-001")).toBeInTheDocument();

    // Select a control and approve
    await user.click(screen.getByRole("button", { name: "REF-001" }));
    await user.type(screen.getByLabelText("Revisado por"), "supervisor-1");
    const approveButtons = screen.getAllByRole("button", { name: "Aprobar" });
    await user.click(approveButtons[0]);

    // Confirm dialog
    expect(await screen.findByRole("dialog")).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("Control aprobado.")).toBeInTheDocument();
    expect(api.approveExternalQualityControl).toHaveBeenCalledWith(
      "eqc-1",
      expect.objectContaining({ reviewedBy: "supervisor-1" }),
    );
  });

  it("shows empty message when no controls are loaded after load button click", async () => {
    vi.spyOn(api, "listExternalQualityControls").mockResolvedValue([]);
    const user = userEvent.setup();
    render(
      <Harness>
        <ExternalQualityControlsScreen />
      </Harness>,
    );
    await user.click(screen.getByRole("button", { name: "Cargar controles" }));
    expect(
      await screen.findByText("No hay registros para el alcance seleccionado."),
    ).toBeInTheDocument();
  });
});
