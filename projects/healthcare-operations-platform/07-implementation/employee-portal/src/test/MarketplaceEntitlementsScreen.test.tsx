import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MarketplaceEntitlementsScreen } from "../components/screens/MarketplaceEntitlementsScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/marketplaceApi";

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

const ENTITLEMENT = {
  entitlementId: "ent-1",
  tenantId: "tenant-1",
  packageId: "pkg-1",
  status: "active",
  grantedAt: "2026-07-25T00:00:00Z",
};

describe("MarketplaceEntitlementsScreen", () => {
  it("grants an entitlement and loads the tenant's entitlements", async () => {
    vi.spyOn(api, "grantEntitlement").mockResolvedValue(ENTITLEMENT);
    vi.spyOn(api, "listTenantEntitlements").mockResolvedValue([ENTITLEMENT]);
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplaceEntitlementsScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del paquete"), "pkg-1");
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-ent-grant-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Otorgar derecho" }));

    expect(await screen.findByText("Derecho otorgado.")).toBeInTheDocument();
    expect(api.grantEntitlement).toHaveBeenCalledWith(
      "tenant-1",
      expect.objectContaining({ packageId: "pkg-1", actorId: "admin-1" }),
    );

    await user.click(screen.getByRole("button", { name: "Cargar derechos" }));
    expect(await screen.findByText("ent-1")).toBeInTheDocument();
  });

  it("revokes a selected entitlement behind a confirmation dialog", async () => {
    vi.spyOn(api, "listTenantEntitlements").mockResolvedValue([ENTITLEMENT]);
    vi.spyOn(api, "revokeEntitlement").mockResolvedValue({
      ...ENTITLEMENT,
      status: "revoked",
      revokedReason: "tenant downgrade",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplaceEntitlementsScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar derechos" }));
    await user.click(await screen.findByRole("button", { name: "ent-1" }));

    await user.type(screen.getByLabelText("Motivo de revocación"), "tenant downgrade");
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-ent-revoke-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Revocar derecho" }));

    expect(await screen.findByRole("dialog")).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("Derecho revocado.")).toBeInTheDocument();
    expect(api.revokeEntitlement).toHaveBeenCalledWith(
      "tenant-1",
      "ent-1",
      expect.objectContaining({ reason: "tenant downgrade", actorId: "admin-1" }),
    );
  });

  it("disables load and grant actions when no tenant scope is set", () => {
    render(
      <LocaleProvider>
        <AdminScopeProvider>
          <MarketplaceEntitlementsScreen />
        </AdminScopeProvider>
      </LocaleProvider>,
    );

    expect(screen.getByRole("button", { name: "Cargar derechos" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Otorgar derecho" })).toBeDisabled();
    expect(screen.getByText("Selecciona un tenant antes de continuar.")).toBeInTheDocument();
  });
});
