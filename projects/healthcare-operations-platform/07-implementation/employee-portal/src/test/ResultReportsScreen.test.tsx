import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ResultReportsScreen } from "../components/screens/ResultReportsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/resultsDeliveryApi";
import type { GeneratedResultReport } from "../api/types";

function ScopedHarness({ children }: { children: ReactNode }) {
  return (
    <AdminScopeProvider>
      <ScopeSetter />
      {children}
    </AdminScopeProvider>
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
  }, [setBranchId, setLaboratoryId, setTenantId]);
  return null;
}

const mockReport: GeneratedResultReport = {
  reportId: "rpt-1",
  resultId: "res-1",
  tenantId: "tenant-1",
  status: "generated",
  generatedAt: "2026-07-17T10:00:00Z",
  generatedBy: "system",
  documentId: "doc-1",
};

describe("ResultReportsScreen", () => {
  it("renders the report history heading", () => {
    render(
      <ScopedHarness>
        <ResultReportsScreen />
      </ScopedHarness>,
    );
    expect(screen.getByRole("heading", { name: "Result Report History" })).toBeInTheDocument();
  });

  it("loads reports for a result", async () => {
    vi.spyOn(api, "listResultReports").mockResolvedValue([mockReport]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultReportsScreen />
      </ScopedHarness>,
    );
    await user.type(screen.getByLabelText("Result ID"), "res-1");
    await user.click(screen.getByRole("button", { name: "Load Reports" }));
    expect(await screen.findByText("Reports loaded.")).toBeInTheDocument();
    expect(screen.getByText("rpt-1")).toBeInTheDocument();
    expect(screen.getByText("system")).toBeInTheDocument();
  });

  it("shows empty state when no reports", async () => {
    vi.spyOn(api, "listResultReports").mockResolvedValue([]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultReportsScreen />
      </ScopedHarness>,
    );
    await user.type(screen.getByLabelText("Result ID"), "res-1");
    await user.click(screen.getByRole("button", { name: "Load Reports" }));
    expect(await screen.findByText(/No se generaron reportes/)).toBeInTheDocument();
  });

  it("triggers report regeneration", async () => {
    vi.spyOn(api, "listResultReports").mockResolvedValue([]);
    vi.spyOn(api, "regenerateReport").mockResolvedValue({ ...mockReport, reportId: "rpt-2" });
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultReportsScreen />
      </ScopedHarness>,
    );
    await user.type(screen.getByLabelText("Result ID"), "res-1");
    await user.click(screen.getByRole("button", { name: "Regenerate Report" }));
    expect(await screen.findByText("Regeneración de reporte iniciada.")).toBeInTheDocument();
    expect(api.regenerateReport).toHaveBeenCalledWith("res-1", "tenant-1", "current_user");
  });
});
