import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ComplianceEvidenceScreen } from "../components/screens/ComplianceEvidenceScreen";
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
  const { setTenantId } = useAdminScope();
  const initialized = useRef(false);
  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;
    setTenantId("tenant-1");
  }, [setTenantId]);
  return null;
}

const EXPORT = {
  exportId: "exp-1",
  tenantId: "tenant-1",
  requestedBy: "auditor-1",
  exportedAt: "2026-07-01T00:00:00Z",
  recordCount: 42,
  status: "completed",
};

describe("ComplianceEvidenceScreen", () => {
  it("searches evidence and shows results", async () => {
    vi.spyOn(api, "searchComplianceEvidence").mockResolvedValue([EXPORT]);
    const user = userEvent.setup();

    render(
      <Harness>
        <ComplianceEvidenceScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Buscar" }));
    expect(await screen.findByText("Evidencia encontrada.")).toBeInTheDocument();
    expect(await screen.findByText("exp-1")).toBeInTheDocument();
  });

  it("exports evidence bundle with confirm dialog", async () => {
    vi.spyOn(api, "exportComplianceEvidence").mockResolvedValue(EXPORT);
    const user = userEvent.setup();

    render(
      <Harness>
        <ComplianceEvidenceScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("Solicitado por"), "auditor-1");
    await user.click(screen.getByRole("button", { name: "Exportar evidencia" }));

    expect(await screen.findByRole("dialog")).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("Paquete de evidencia exportado.")).toBeInTheDocument();
    expect(api.exportComplianceEvidence).toHaveBeenCalledWith(
      expect.objectContaining({ requestedBy: "auditor-1" }),
    );
  });

  it("loads compliance documents", async () => {
    vi.spyOn(api, "searchDocuments").mockResolvedValue([
      {
        documentId: "doc-1",
        tenantId: "tenant-1",
        fileName: "evidence-bundle.zip",
        contentType: "application/zip",
        storedAt: "2026-07-01",
        version: 1,
      },
    ]);
    const user = userEvent.setup();

    render(
      <Harness>
        <ComplianceEvidenceScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar documentos" }));
    expect(await screen.findByText("evidence-bundle.zip")).toBeInTheDocument();
  });
});
