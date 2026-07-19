import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MigrationJobsScreen } from "../components/screens/MigrationJobsScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/integrationMigrationApi";

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
  }, [setLaboratoryId, setTenantId]);
  return null;
}

describe("MigrationJobsScreen", () => {
  it("creates and loads migration jobs", async () => {
    vi.spyOn(api, "createMigrationJob").mockResolvedValue({
      migrationJobId: "job-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      sourceSystemName: "Legacy LIS",
      status: "CREATED",
    });
    vi.spyOn(api, "listMigrationJobs").mockResolvedValue([
      {
        migrationJobId: "job-1",
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        sourceSystemName: "Legacy LIS",
        status: "CREATED",
      },
    ]);
    const user = userEvent.setup();

    render(
      <Harness>
        <MigrationJobsScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("Sistema origen"), "Legacy LIS");
    await user.click(screen.getByRole("button", { name: "Crear" }));
    expect(await screen.findByText("Trabajo de migración creado.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cargar trabajos" }));
    expect(await screen.findByText("Legacy LIS")).toBeInTheDocument();
  });

  it("uploads packages and executes validation, approval, commit and reconciliation", async () => {
    vi.spyOn(api, "receiveImportPackage").mockResolvedValue({
      importBatchId: "batch-1",
      migrationJobId: "job-1",
      storedPackageReference: "memory://batch-1",
      entityCounts: { patients: 2 },
    });
    vi.spyOn(api, "runDryRunValidation").mockResolvedValue({
      reportId: "report-1",
      importBatchId: "batch-1",
      structuralErrors: [],
      rowLevelErrors: [],
      rowLevelWarnings: ["missing optional phone"],
      validationCategoriesEvaluated: ["structure"],
      passed: true,
    });
    vi.spyOn(api, "approveImport").mockResolvedValue({
      migrationJobId: "job-1",
      status: "APPROVED",
    });
    vi.spyOn(api, "commitImport").mockResolvedValue({
      executionId: "exec-1",
      migrationJobId: "job-1",
      attemptNumber: 1,
      domainCommandsInvoked: ["RegisterPatient"],
      checkpoint: "patients:2",
      status: "COMPLETED",
    });
    vi.spyOn(api, "listReconciliationReports").mockResolvedValue([
      {
        reconciliationReportId: "rec-1",
        migrationJobId: "job-1",
        phase: "post-import",
        importedCounts: { patients: 2 },
        rejectedCounts: {},
        skippedCounts: {},
        warningCounts: { patients: 1 },
      },
    ]);
    const user = userEvent.setup();

    render(
      <Harness>
        <MigrationJobsScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del trabajo"), "job-1");
    await user.upload(
      screen.getByLabelText("Manifest"),
      new File(["{}"], "manifest.json", { type: "application/json" }),
    );
    await user.upload(
      screen.getByLabelText("Paquete"),
      new File(["id,name\n1,Ada"], "patients.csv", { type: "text/csv" }),
    );
    await user.click(screen.getByRole("button", { name: "Subir paquete" }));
    expect(await screen.findByText("Paquete recibido.")).toBeInTheDocument();
    expect(screen.getByText("batch-1: patients: 2")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Ejecutar validación previa" }));
    expect(await screen.findByText("Validación previa ejecutada.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Aprobar" }));
    expect(await screen.findByText("Importación aprobada.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("Importación confirmada.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cargar reconciliación" }));
    expect(await screen.findByText("Reconciliación cargada.")).toBeInTheDocument();
    expect(screen.getByText("post-import")).toBeInTheDocument();
  });
});
