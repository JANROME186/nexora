import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AuditManagementScreen } from "../components/screens/AuditManagementScreen";
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

const AUDIT = {
  auditId: "audit-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  auditType: "internal",
  auditorName: "Dr. Reyes",
  status: "planned",
  findings: [],
  version: 1,
};

describe("AuditManagementScreen", () => {
  it("plans an audit, loads audits and records a finding for selected audit", async () => {
    vi.spyOn(api, "planQualityAudit").mockResolvedValue(AUDIT);
    vi.spyOn(api, "listQualityAudits").mockResolvedValue([AUDIT]);
    vi.spyOn(api, "recordAuditFinding").mockResolvedValue({
      ...AUDIT,
      status: "findings_recorded",
      findings: [
        {
          findingId: "f-1",
          category: "documentation",
          description: "Missing SOP",
          severity: "minor",
        },
      ],
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <AuditManagementScreen />
      </Harness>,
    );

    // Plan
    await user.type(screen.getByLabelText("Tipo de auditoría"), "internal");
    await user.type(screen.getByLabelText("Nombre del auditor"), "Dr. Reyes");
    await user.click(screen.getByRole("button", { name: "Planificar" }));
    expect(await screen.findByText("Auditoría planificada.")).toBeInTheDocument();

    // Load
    await user.click(screen.getByRole("button", { name: "Cargar auditorías" }));
    expect(await screen.findByText("Dr. Reyes")).toBeInTheDocument();

    // Select and record finding
    await user.click(screen.getByRole("button", { name: "Dr. Reyes" }));
    await user.type(screen.getByLabelText("Categoría del hallazgo"), "documentation");
    await user.type(screen.getByLabelText("Descripción del hallazgo"), "Missing SOP");
    await user.type(screen.getByLabelText("Gravedad"), "minor");
    await user.click(screen.getByRole("button", { name: "Registrar hallazgo" }));
    expect(await screen.findByText("Hallazgo registrado.")).toBeInTheDocument();
    expect(api.recordAuditFinding).toHaveBeenCalledWith(
      "audit-1",
      expect.objectContaining({ category: "documentation" }),
    );
  });

  it("closes an audit with confirm dialog", async () => {
    vi.spyOn(api, "listQualityAudits").mockResolvedValue([AUDIT]);
    vi.spyOn(api, "closeQualityAudit").mockResolvedValue({ ...AUDIT, status: "closed" });
    const user = userEvent.setup();

    render(
      <Harness>
        <AuditManagementScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar auditorías" }));
    await user.click(await screen.findByRole("button", { name: "Dr. Reyes" }));
    await user.type(screen.getByLabelText("Cerrado por"), "director-1");
    await user.click(screen.getByRole("button", { name: "Cerrar auditoría" }));

    expect(await screen.findByRole("dialog")).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("Auditoría cerrada.")).toBeInTheDocument();
  });

  it("shows empty state after loading when no audits exist", async () => {
    vi.spyOn(api, "listQualityAudits").mockResolvedValue([]);
    const user = userEvent.setup();
    render(
      <Harness>
        <AuditManagementScreen />
      </Harness>,
    );
    await user.click(screen.getByRole("button", { name: "Cargar auditorías" }));
    expect(
      await screen.findByText("No hay registros para el alcance seleccionado."),
    ).toBeInTheDocument();
  });
});
