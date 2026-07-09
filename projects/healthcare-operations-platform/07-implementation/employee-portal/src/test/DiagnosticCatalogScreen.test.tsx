import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { DiagnosticCatalogScreen } from "../components/screens/DiagnosticCatalogScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/catalogApi";

function ScopedCatalogHarness({ children }: { children: ReactNode }) {
  return (
    <AdminScopeProvider>
      <ScopeSetter />
      {children}
    </AdminScopeProvider>
  );
}

function ScopeSetter() {
  const { setTenantId, setLaboratoryId } = useAdminScope();
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) {
      return;
    }
    initialized.current = true;
    setTenantId("tenant-1");
    setLaboratoryId("lab-1");
  }, [setLaboratoryId, setTenantId]);

  return null;
}

describe("DiagnosticCatalogScreen", () => {
  it("creates a diagnostic service in the active laboratory scope", async () => {
    vi.spyOn(api, "createDiagnosticService").mockResolvedValue({
      serviceId: "service-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      code: "GLU",
      nameEn: "Glucose",
      nameEs: "Glucosa",
      serviceType: "laboratory",
      components: [{ componentType: "test", componentRefId: "GLU-TEST", displayOrder: 1 }],
      status: "draft",
      version: 1
    });

    const user = userEvent.setup();
    render(
      <ScopedCatalogHarness>
        <DiagnosticCatalogScreen />
      </ScopedCatalogHarness>
    );

    await user.type(screen.getByLabelText("Code"), "GLU");
    await user.type(screen.getByLabelText("Name EN"), "Glucose");
    await user.type(screen.getByLabelText("Name ES"), "Glucosa");
    await user.click(screen.getByRole("button", { name: "Create catalog item" }));

    expect(api.createDiagnosticService).toHaveBeenCalledWith({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      code: "GLU",
      nameEn: "Glucose",
      nameEs: "Glucosa",
      serviceType: "laboratory",
      components: [{ componentType: "test", componentRefId: "GLU-TEST", displayOrder: 1 }]
    });
    expect(await screen.findByText("Catalog item created.")).toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "service-1" })).toBeInTheDocument();
  });
});
