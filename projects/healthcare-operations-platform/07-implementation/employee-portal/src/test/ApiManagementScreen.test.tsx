import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ApiManagementScreen } from "../components/screens/ApiManagementScreen";
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
  const { setTenantId } = useAdminScope();
  const initialized = useRef(false);
  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;
    setTenantId("tenant-1");
  }, [setTenantId]);
  return null;
}

describe("ApiManagementScreen", () => {
  it("loads operations and classifies an operation", async () => {
    vi.spyOn(api, "listApiOperations").mockResolvedValue([
      {
        registrationId: "reg-1",
        tenantId: "tenant-1",
        ownerCapability: "BCM-PLT-005",
        operationId: "GET /partner/results",
        classification: "partner",
        apiVersion: "v1",
        deprecationStatus: "ACTIVE",
      },
    ]);
    vi.spyOn(api, "classifyApiOperation").mockResolvedValue({
      registrationId: "reg-2",
      tenantId: "tenant-1",
      ownerCapability: "BCM-PLT-010",
      operationId: "POST /migration/jobs",
      classification: "internal",
      apiVersion: "v1",
      deprecationStatus: "ACTIVE",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <ApiManagementScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar operaciones" }));
    expect(await screen.findByText("GET /partner/results")).toBeInTheDocument();

    await user.clear(screen.getByLabelText("ID de operación"));
    await user.type(screen.getByLabelText("ID de operación"), "POST /migration/jobs");
    await user.clear(screen.getByLabelText("Capability propietaria"));
    await user.type(screen.getByLabelText("Capability propietaria"), "BCM-PLT-010");
    await user.clear(screen.getByLabelText("Clasificación"));
    await user.type(screen.getByLabelText("Clasificación"), "internal");
    await user.click(screen.getByRole("button", { name: "Crear" }));

    expect(await screen.findByText("Operación clasificada.")).toBeInTheDocument();
    expect(api.classifyApiOperation).toHaveBeenCalledWith(
      "POST /migration/jobs",
      expect.objectContaining({
        ownerCapability: "BCM-PLT-010",
        classification: "internal",
        tenantId: "tenant-1",
      }),
    );
  });

  it("issues, revokes and rate-limits partner API keys", async () => {
    vi.spyOn(api, "issuePartnerApiKey").mockResolvedValue({
      keyId: "key-1",
      tenantId: "tenant-1",
      consumerName: "Partner LIS",
      grantedScopes: ["integration:read"],
      status: "ACTIVE",
    });
    vi.spyOn(api, "revokePartnerApiKey").mockResolvedValue({
      keyId: "key-1",
      tenantId: "tenant-1",
      consumerName: "Partner LIS",
      grantedScopes: ["integration:read"],
      status: "REVOKED",
    });
    vi.spyOn(api, "setRateLimitPolicy").mockResolvedValue({
      policyId: "policy-1",
      classification: "partner",
      requestsPerMinute: 90,
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <ApiManagementScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("Consumidor"), "Partner LIS");
    await user.click(screen.getByRole("button", { name: "Emitir llave" }));
    expect(await screen.findByText("Llave emitida.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Revocar llave" }));
    expect(await screen.findByText("Llave revocada.")).toBeInTheDocument();

    await user.clear(screen.getByLabelText("Solicitudes por minuto"));
    await user.type(screen.getByLabelText("Solicitudes por minuto"), "90");
    await user.click(screen.getByRole("button", { name: "Actualizar límite" }));
    expect(await screen.findByText("partner: 90")).toBeInTheDocument();
  });
});
