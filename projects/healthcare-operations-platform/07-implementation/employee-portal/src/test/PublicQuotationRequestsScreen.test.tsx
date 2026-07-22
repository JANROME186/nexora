import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PublicQuotationRequestsScreen } from "../components/screens/PublicQuotationRequestsScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/publicRequestsApi";
import type { QuotationRequest } from "../api/types";

function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>{children}</AdminScopeProvider>
    </LocaleProvider>
  );
}

function ScopedHarness({ children }: { children: ReactNode }) {
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
    if (initialized.current) {
      return;
    }
    initialized.current = true;
    setTenantId("tenant-1");
  }, [setTenantId]);

  return null;
}

const publicDraft: QuotationRequest = {
  quotationId: "quote-public-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  prospectiveFullName: "Grace Hopper",
  prospectiveEmail: "grace@example.com",
  channel: "public_website",
  status: "draft",
  version: 1,
};

const staffDraft: QuotationRequest = {
  quotationId: "quote-staff-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  branchId: "branch-1",
  patientId: "patient-1",
  channel: "employee_portal",
  status: "draft",
  version: 1,
};

describe("PublicQuotationRequestsScreen", () => {
  it("requires a tenant scope before loading", () => {
    render(
      <Harness>
        <PublicQuotationRequestsScreen />
      </Harness>,
    );

    expect(screen.getByText("Selecciona un tenant antes de continuar.")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Cargar solicitudes" })).toBeDisabled();
  });

  it("loads the queue and filters out non-public or already-actioned quotations", async () => {
    vi.spyOn(api, "listQuotations").mockResolvedValue([publicDraft, staffDraft]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicQuotationRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));

    expect(await screen.findByText("Solicitudes cargadas.")).toBeInTheDocument();
    expect(screen.getByText("Grace Hopper")).toBeInTheDocument();
    expect(screen.queryByText("quote-staff-1")).not.toBeInTheDocument();
  });

  it("shows the empty state when there are no pending public quotation requests", async () => {
    vi.spyOn(api, "listQuotations").mockResolvedValue([staffDraft]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicQuotationRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));

    expect(
      await screen.findByText("No hay solicitudes públicas de cotización pendientes."),
    ).toBeInTheDocument();
  });

  it("issues a selected quotation only after the confirmation dialog is accepted", async () => {
    vi.spyOn(api, "listQuotations").mockResolvedValue([publicDraft]);
    vi.spyOn(api, "issueQuotation").mockResolvedValue({
      ...publicDraft,
      status: "issued",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicQuotationRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));
    await user.click(await screen.findByRole("button", { name: "quote-public-1" }));
    await user.click(screen.getByRole("button", { name: "Emitir cotización" }));

    expect(screen.getByRole("dialog", { name: "Emitir cotización" })).toBeInTheDocument();
    expect(api.issueQuotation).not.toHaveBeenCalled();

    await user.click(screen.getAllByRole("button", { name: "Confirmar" }).slice(-1)[0]);

    expect(await screen.findByText("Cotización emitida.")).toBeInTheDocument();
    expect(api.issueQuotation).toHaveBeenCalledWith("quote-public-1", {});
    expect(screen.queryByRole("button", { name: "quote-public-1" })).not.toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "issued" })).toBeInTheDocument();
  });

  it("rejects a selected quotation with a reason code after confirmation", async () => {
    vi.spyOn(api, "listQuotations").mockResolvedValue([publicDraft]);
    vi.spyOn(api, "cancelQuotation").mockResolvedValue({
      ...publicDraft,
      status: "cancelled",
      cancellationReason: "not reachable",
      version: 2,
    });

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicQuotationRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));
    await user.click(await screen.findByRole("button", { name: "quote-public-1" }));
    await user.type(screen.getByLabelText("Motivo de rechazo"), "not reachable");
    await user.click(screen.getByRole("button", { name: "Rechazar solicitud" }));
    await user.click(screen.getAllByRole("button", { name: "Confirmar" }).slice(-1)[0]);

    expect(await screen.findByText("Solicitud rechazada.")).toBeInTheDocument();
    expect(api.cancelQuotation).toHaveBeenCalledWith("quote-public-1", {
      reasonCode: "not reachable",
    });
  });

  it("surfaces an error when loading the queue fails", async () => {
    vi.spyOn(api, "listQuotations").mockRejectedValue(
      new ApiError(500, "Unexpected failure while loading quotations."),
    );

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicQuotationRequestsScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar solicitudes" }));

    expect(
      await screen.findByText("Unexpected failure while loading quotations."),
    ).toBeInTheDocument();
  });
});
