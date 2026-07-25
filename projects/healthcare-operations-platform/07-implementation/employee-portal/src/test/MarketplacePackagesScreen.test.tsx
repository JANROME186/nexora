import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MarketplacePackagesScreen } from "../components/screens/MarketplacePackagesScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider } from "../state/AdminScopeContext";
import * as api from "../api/marketplaceApi";

function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>{children}</AdminScopeProvider>
    </LocaleProvider>
  );
}

const PACKAGE = {
  packageId: "pkg-1",
  code: "LAB-CORE",
  name: "Lab Core Package",
  category: "clinical",
  capabilityMappings: ["BCM-LAB-001"],
  status: "draft",
};

const VERSION = {
  versionId: "ver-1",
  packageId: "pkg-1",
  version: "1.0.0",
  lifecycleStatus: "draft",
  compatibilityApproved: false,
  securityReviewApproved: false,
  supportModelApproved: false,
  telemetryModelApproved: false,
};

describe("MarketplacePackagesScreen", () => {
  it("submits a package and loads the catalog", async () => {
    vi.spyOn(api, "submitPackage").mockResolvedValue(PACKAGE);
    vi.spyOn(api, "listPublishedPackages").mockResolvedValue([PACKAGE]);
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplacePackagesScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("Código"), "LAB-CORE");
    await user.type(screen.getByLabelText("Nombre"), "Lab Core Package");
    await user.type(screen.getByLabelText("Categoría"), "clinical");
    await user.type(
      screen.getByLabelText("Capacidades vinculadas (separadas por coma)"),
      "BCM-LAB-001",
    );
    await user.type(screen.getByLabelText("Versión inicial"), "1.0.0");
    await user.type(screen.getByLabelText("ID del actor"), "admin-1");
    await user.click(screen.getByRole("button", { name: "Enviar paquete" }));

    expect(await screen.findByText("Paquete enviado.")).toBeInTheDocument();
    expect(api.submitPackage).toHaveBeenCalledWith(
      expect.objectContaining({ code: "LAB-CORE", capabilityMappings: ["BCM-LAB-001"] }),
    );

    await user.click(screen.getByRole("button", { name: "Cargar paquetes" }));
    expect(await screen.findByText("LAB-CORE")).toBeInTheDocument();
  });

  it("publishes, views, certifies and retires a version after selecting a package", async () => {
    vi.spyOn(api, "listPublishedPackages").mockResolvedValue([PACKAGE]);
    vi.spyOn(api, "publishPackage").mockResolvedValue({ ...PACKAGE, status: "published" });
    vi.spyOn(api, "getPackageVersion").mockResolvedValue(VERSION);
    vi.spyOn(api, "certifyPackageVersion").mockResolvedValue({
      ...VERSION,
      lifecycleStatus: "certified",
    });
    vi.spyOn(api, "retirePackageVersion").mockResolvedValue({
      ...VERSION,
      lifecycleStatus: "retired",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplacePackagesScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar paquetes" }));
    await user.click(await screen.findByRole("button", { name: "LAB-CORE" }));

    // Publish version
    await user.type(
      screen.getByLabelText("Versión", { selector: "#mkt-pkg-publish-version" }),
      "1.0.0",
    );
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-pkg-publish-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Publicar versión" }));
    expect(await screen.findByText("Versión publicada.")).toBeInTheDocument();

    // View version
    await user.type(
      screen.getByLabelText("Versión", { selector: "#mkt-pkg-view-version" }),
      "1.0.0",
    );
    await user.click(screen.getByRole("button", { name: "Ver versión" }));
    expect(await screen.findByText("Versión cargada.")).toBeInTheDocument();

    // Certify version
    await user.click(screen.getByLabelText("Compatibilidad aprobada"));
    await user.click(screen.getByLabelText("Revisión de seguridad aprobada"));
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-pkg-certify-actor" }),
      "qa-1",
    );
    await user.click(screen.getByRole("button", { name: "Certificar versión" }));
    expect(await screen.findByText("Versión certificada.")).toBeInTheDocument();
    expect(api.certifyPackageVersion).toHaveBeenCalledWith(
      "pkg-1",
      "1.0.0",
      expect.objectContaining({ compatibilityApproved: true, securityReviewApproved: true }),
    );

    // Retire version (sensitive action, confirm dialog)
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-pkg-retire-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Retirar versión" }));
    expect(await screen.findByRole("dialog")).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("Versión retirada.")).toBeInTheDocument();
    expect(api.retirePackageVersion).toHaveBeenCalledWith(
      "pkg-1",
      "1.0.0",
      expect.objectContaining({ actorId: "admin-1" }),
    );
  });
});
