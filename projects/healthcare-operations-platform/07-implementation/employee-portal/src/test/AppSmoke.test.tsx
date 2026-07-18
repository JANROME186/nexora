import { beforeEach, describe, expect, it } from "vitest";
import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { App } from "../App";

// Navigation tab labels come from the default locale (es-MX) via LocaleContext (see
// src/i18n/locales/es-MX.ts appShell.tabs); screen headings themselves remain inline English JSX
// text, unaffected by the locale switch (out of scope for this foundation-alignment slice,
// tracked as remaining TD-I18N-002 scope).
describe("Employee portal app smoke", () => {
  beforeEach(() => {
    // LocaleContext persists the selected locale to localStorage; reset it between tests so
    // each test starts from the default es-MX locale regardless of test execution order.
    window.localStorage.clear();
  });

  it("renders the administration shell and navigates across Platform Foundation screens", async () => {
    const user = userEvent.setup();
    render(<App />);

    expect(
      screen.getByRole("heading", {
        name: "Plataforma de Operaciones de Salud - Administración del Portal de Empleados",
      }),
    ).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Platform Tenant List" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Laboratorios" }));
    expect(screen.getByRole("heading", { name: "Laboratory List" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Sucursales" }));
    expect(screen.getByRole("heading", { name: "Branch List" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Usuarios" }));
    expect(screen.getByRole("heading", { name: "User Management" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Asignación de Roles" }));
    expect(screen.getByRole("heading", { name: "Role Assignment" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Eventos de Auditoría" }));
    expect(screen.getByRole("heading", { name: "Audit Search" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Catálogo Diagnóstico" }));
    expect(screen.getByRole("heading", { name: "Diagnostic Catalog" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Búsqueda de Personas" }));
    expect(
      screen.getByRole("heading", { name: "People Search and Duplicate Resolution" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Pacientes" }));
    expect(screen.getByRole("heading", { name: "Patients" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Doctores" }));
    expect(screen.getByRole("heading", { name: "Doctors" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Registros de Pacientes" }));
    expect(screen.getByRole("heading", { name: "Patient Registrations" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Recepción" }));
    expect(screen.getByRole("heading", { name: "Front Desk Worklist" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Órdenes Diagnósticas" }));
    expect(screen.getByRole("heading", { name: "Diagnostic Orders" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Sesiones de Caja" }));
    expect(screen.getByRole("heading", { name: "Cash Sessions" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Ventas" }));
    expect(screen.getByRole("heading", { name: "Sales" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Solicitudes de Facturación" }));
    expect(screen.getByRole("heading", { name: "Billing Requests" })).toBeInTheDocument();
  });

  it("navigates to Results and Digital Delivery screens (MVP-MOD-007)", async () => {
    const user = userEvent.setup();
    render(<App />);

    await user.click(screen.getByRole("button", { name: "Búsqueda de Resultados" }));
    expect(screen.getByRole("heading", { name: "Result Search and Worklist" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Reportes de Resultados" }));
    expect(screen.getByRole("heading", { name: "Result Report History" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Escalaciones Críticas" }));
    expect(
      screen.getByRole("heading", { name: "Critical Result Escalation Worklist" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Notificaciones de Resultados" }));
    expect(
      screen.getByRole("heading", { name: "Result Notification History" }),
    ).toBeInTheDocument();
  });

  it("switches the interface language via the AppShell language control", async () => {
    const user = userEvent.setup();
    render(<App />);

    expect(screen.getByRole("button", { name: "Sucursales" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "EN" }));

    expect(
      screen.getByRole("heading", {
        name: "Healthcare Operations Platform - Employee Portal Administration",
      }),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Branches" })).toBeInTheDocument();
  });

  it("only renders tabs the current session has permission for", () => {
    render(<App />);

    // The local dev fixture session defaults to ADMIN, which is granted every screen
    // permission, so all 27 navigation tabs remain visible.
    const nav = screen.getByRole("navigation", { name: "Pantallas de administración" });
    expect(within(nav).getAllByRole("button")).toHaveLength(27);
  });
});
