import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QualityEventIntakeScreen } from "../components/screens/QualityEventIntakeScreen";
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
  const { setTenantId, setLaboratoryId } = useAdminScope();
  const initialized = useRef(false);
  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;
    setTenantId("tenant-1");
    setLaboratoryId("lab-1");
  }, [setTenantId, setLaboratoryId]);
  return null;
}

const EVENT = {
  qualityEventId: "qev-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  eventType: "near_miss",
  description: "Sample mislabeled",
  reportedBy: "tech-1",
  status: "open",
  version: 1,
};

describe("QualityEventIntakeScreen", () => {
  it("records a quality event and loads events", async () => {
    vi.spyOn(api, "recordQualityEvent").mockResolvedValue(EVENT);
    vi.spyOn(api, "listQualityEvents").mockResolvedValue([EVENT]);
    const user = userEvent.setup();

    render(
      <Harness>
        <QualityEventIntakeScreen />
      </Harness>,
    );

    // Record
    await user.type(screen.getByLabelText("Tipo de evento"), "near_miss");
    await user.type(screen.getByLabelText("Descripción"), "Sample mislabeled");
    await user.type(screen.getByLabelText("Reportado por"), "tech-1");
    await user.click(screen.getByRole("button", { name: "Registrar evento" }));
    expect(await screen.findByText("Evento registrado.")).toBeInTheDocument();

    // Load
    await user.click(screen.getByRole("button", { name: "Cargar eventos" }));
    expect(await screen.findByText("near_miss")).toBeInTheDocument();
  });

  it("links a selected event to an investigation with confirm dialog", async () => {
    vi.spyOn(api, "listQualityEvents").mockResolvedValue([EVENT]);
    vi.spyOn(api, "linkQualityEvent").mockResolvedValue({
      ...EVENT,
      status: "linked",
      linkedInvestigationId: "capa-5",
      linkedInvestigationType: "capa",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <QualityEventIntakeScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar eventos" }));
    await user.click(await screen.findByRole("button", { name: "near_miss" }));
    await user.type(screen.getByLabelText("ID de investigación vinculada"), "capa-5");
    await user.type(screen.getByLabelText("Tipo de investigación"), "capa");
    await user.click(screen.getByRole("button", { name: "Vincular a investigación" }));

    expect(await screen.findByRole("dialog")).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Confirmar" }));
    expect(await screen.findByText("Evento vinculado a investigación.")).toBeInTheDocument();
    expect(api.linkQualityEvent).toHaveBeenCalledWith(
      "qev-1",
      expect.objectContaining({ linkedInvestigationId: "capa-5" }),
    );
  });

  it("shows empty state when no events loaded after load button click", async () => {
    vi.spyOn(api, "listQualityEvents").mockResolvedValue([]);
    const user = userEvent.setup();
    render(
      <Harness>
        <QualityEventIntakeScreen />
      </Harness>,
    );
    await user.click(screen.getByRole("button", { name: "Cargar eventos" }));
    expect(
      await screen.findByText("No hay registros para el alcance seleccionado."),
    ).toBeInTheDocument();
  });
});
