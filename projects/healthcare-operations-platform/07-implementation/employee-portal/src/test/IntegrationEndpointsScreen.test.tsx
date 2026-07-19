import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { IntegrationEndpointsScreen } from "../components/screens/IntegrationEndpointsScreen";
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

describe("IntegrationEndpointsScreen", () => {
  it("loads integration endpoints and retires the selected endpoint", async () => {
    vi.spyOn(api, "listIntegrationEndpoints").mockResolvedValue([
      {
        endpointId: "endpoint-1",
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        endpointName: "Hospital HL7",
        protocol: "HL7V2",
        direction: "INBOUND",
        status: "ACTIVE",
      },
    ]);
    vi.spyOn(api, "retireIntegrationEndpoint").mockResolvedValue({
      endpointId: "endpoint-1",
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      endpointName: "Hospital HL7",
      protocol: "HL7V2",
      direction: "INBOUND",
      status: "RETIRED",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <IntegrationEndpointsScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar endpoints" }));
    expect(await screen.findByText("Hospital HL7")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "endpoint-1" }));
    await user.click(screen.getByRole("button", { name: "Retirar" }));
    expect(await screen.findByText("Endpoint retirado.")).toBeInTheDocument();
    expect(api.retireIntegrationEndpoint).toHaveBeenCalledWith("endpoint-1", "current_user");
  });

  it("receives and retries integration messages", async () => {
    vi.spyOn(api, "listIntegrationEndpoints").mockResolvedValue([
      {
        endpointId: "endpoint-1",
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        endpointName: "Hospital HL7",
        protocol: "HL7V2",
        direction: "INBOUND",
        status: "ACTIVE",
      },
    ]);
    vi.spyOn(api, "receiveIntegrationMessage").mockResolvedValue({
      messageId: "msg-1",
      endpointId: "endpoint-1",
      externalMessageId: "ext-1",
      normalizationStatus: "ACKNOWLEDGED",
      retryCount: 0,
    });
    vi.spyOn(api, "retryIntegrationMessage").mockResolvedValue({
      messageId: "msg-1",
      endpointId: "endpoint-1",
      externalMessageId: "ext-1",
      normalizationStatus: "ACKNOWLEDGED",
      retryCount: 1,
      canonicalFields: { patientId: "patient-1" },
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <IntegrationEndpointsScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar endpoints" }));
    await user.click(await screen.findByRole("button", { name: "endpoint-1" }));
    await user.type(screen.getByLabelText("ID externo del mensaje"), "ext-1");
    await user.type(screen.getByLabelText("Payload crudo"), "MSH|sample");
    await user.click(screen.getByRole("button", { name: "Recibir mensaje" }));
    expect(await screen.findByText("Mensaje recibido.")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Reintentar" }));
    expect(await screen.findByText("Mensaje reintentado.")).toBeInTheDocument();
    expect(screen.getByText("patientId: patient-1")).toBeInTheDocument();
  });
});
