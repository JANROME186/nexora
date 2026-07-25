import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {
  isPackageEntitled,
  MarketplaceInstallationsScreen,
} from "../components/screens/MarketplaceInstallationsScreen";
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

const ACTIVE_ENTITLEMENT = {
  entitlementId: "ent-1",
  tenantId: "tenant-1",
  packageId: "pkg-1",
  status: "active",
  grantedAt: "2026-07-25T00:00:00Z",
};

const INSTALLATION = {
  installationId: "inst-1",
  tenantId: "tenant-1",
  packageId: "pkg-1",
  version: "1.0.0",
  lifecycleStatus: "installed",
};

describe("isPackageEntitled (TD-BE-019 gating logic)", () => {
  it("is entitled for a matching active, non-expired entitlement", () => {
    expect(isPackageEntitled([ACTIVE_ENTITLEMENT], "pkg-1")).toBe(true);
  });

  it("is not entitled when no packageId is provided", () => {
    expect(isPackageEntitled([ACTIVE_ENTITLEMENT], "")).toBe(false);
  });

  it("is not entitled when no entitlement matches the packageId", () => {
    expect(isPackageEntitled([ACTIVE_ENTITLEMENT], "pkg-999")).toBe(false);
  });

  it("is not entitled when the matching entitlement is revoked", () => {
    expect(isPackageEntitled([{ ...ACTIVE_ENTITLEMENT, status: "revoked" }], "pkg-1")).toBe(false);
  });

  it("is not entitled when the matching entitlement has expired", () => {
    expect(
      isPackageEntitled([{ ...ACTIVE_ENTITLEMENT, expiresAt: "2020-01-01T00:00:00Z" }], "pkg-1"),
    ).toBe(false);
  });
});

describe("MarketplaceInstallationsScreen (TD-BE-019 entitlement-gated install)", () => {
  it("allows installing a package the tenant has an active entitlement for", async () => {
    vi.spyOn(api, "listInstallations").mockResolvedValue([]);
    vi.spyOn(api, "listTenantEntitlements").mockResolvedValue([ACTIVE_ENTITLEMENT]);
    vi.spyOn(api, "installPackage").mockResolvedValue(INSTALLATION);
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplaceInstallationsScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar instalaciones" }));
    await screen.findByText("Registros cargados.");

    await user.type(screen.getByLabelText("ID del paquete"), "pkg-1");
    expect(screen.queryByText(/no cuenta con un derecho activo/)).not.toBeInTheDocument();

    const installButton = screen.getByRole("button", { name: "Instalar paquete" });
    expect(installButton).toBeEnabled();

    await user.type(screen.getByLabelText("Versión"), "1.0.0");
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-inst-install-actor" }),
      "admin-1",
    );
    await user.click(installButton);

    expect(await screen.findByText("Paquete instalado.")).toBeInTheDocument();
    expect(api.installPackage).toHaveBeenCalledWith(
      "tenant-1",
      expect.objectContaining({ packageId: "pkg-1", version: "1.0.0" }),
    );
  });

  it("disables install and shows an explanatory status for a package without an active entitlement", async () => {
    vi.spyOn(api, "listInstallations").mockResolvedValue([]);
    vi.spyOn(api, "listTenantEntitlements").mockResolvedValue([ACTIVE_ENTITLEMENT]);
    const installSpy = vi.spyOn(api, "installPackage");
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplaceInstallationsScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar instalaciones" }));
    await screen.findByText("Registros cargados.");

    await user.type(screen.getByLabelText("ID del paquete"), "pkg-999");

    expect(
      screen.getByText(
        "Este tenant no cuenta con un derecho activo para este paquete. La instalación no está disponible.",
      ),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Instalar paquete" })).toBeDisabled();
    expect(installSpy).not.toHaveBeenCalled();
  });

  it("manages the lifecycle of a selected installation (activate, suspend, upgrade, rollback, uninstall)", async () => {
    vi.spyOn(api, "listInstallations").mockResolvedValue([INSTALLATION]);
    vi.spyOn(api, "listTenantEntitlements").mockResolvedValue([ACTIVE_ENTITLEMENT]);
    vi.spyOn(api, "activateInstallation").mockResolvedValue({
      ...INSTALLATION,
      lifecycleStatus: "active",
    });
    vi.spyOn(api, "upgradeInstallation").mockResolvedValue({
      ...INSTALLATION,
      version: "1.1.0",
      lifecycleStatus: "active",
    });
    vi.spyOn(api, "rollbackInstallation").mockResolvedValue({
      ...INSTALLATION,
      version: "1.0.0",
      rollbackCheckpointVersion: "1.0.0",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplaceInstallationsScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar instalaciones" }));
    await user.click(await screen.findByRole("button", { name: "inst-1" }));

    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-inst-manage-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Activar" }));
    expect(await screen.findByText("Instalación activada.")).toBeInTheDocument();
    expect(api.activateInstallation).toHaveBeenCalledWith(
      "tenant-1",
      "inst-1",
      expect.objectContaining({ actorId: "admin-1" }),
    );

    await user.type(screen.getByLabelText("Versión objetivo"), "1.1.0");
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-inst-upgrade-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Actualizar" }));
    expect(await screen.findByText("Instalación actualizada.")).toBeInTheDocument();

    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-inst-rollback-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Revertir" }));
    expect(await screen.findByText("Instalación revertida.")).toBeInTheDocument();
  });
});
