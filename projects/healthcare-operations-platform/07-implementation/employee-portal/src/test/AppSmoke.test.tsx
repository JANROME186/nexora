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
    expect(
      screen.getByRole("heading", { name: "Búsqueda de Eventos de Auditoría" }),
    ).toBeInTheDocument();

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

    await user.click(screen.getByRole("button", { name: "Citas" }));
    expect(screen.getByRole("heading", { name: "Appointments" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Admisiones" }));
    expect(screen.getByRole("heading", { name: "Admissions" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cotizaciones" }));
    expect(screen.getByRole("heading", { name: "Quotations" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Órdenes Diagnósticas" }));
    expect(screen.getByRole("heading", { name: "Diagnostic Orders" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Sesiones de Caja" }));
    expect(screen.getByRole("heading", { name: "Cash Sessions" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Ventas" }));
    expect(screen.getByRole("heading", { name: "Sales" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Solicitudes de Facturación" }));
    expect(screen.getByRole("heading", { name: "Billing Requests" })).toBeInTheDocument();
  }, 35000);

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

  it("navigates to Integration and Migration Readiness screens (MVP-MOD-008)", async () => {
    const user = userEvent.setup();
    render(<App />);

    await user.click(screen.getByRole("button", { name: "Integraciones" }));
    expect(
      screen.getByRole("heading", { name: "Administración de Integraciones" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Gobierno de APIs" }));
    expect(screen.getByRole("heading", { name: "Gobierno de APIs" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Migraciones" }));
    expect(
      screen.getByRole("heading", { name: "Administración de Migraciones" }),
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

  it("navigates to Inventory and Internal Quality screens (COM-MOD-010)", async () => {
    const user = userEvent.setup();
    render(<App />);

    await user.click(screen.getByRole("button", { name: "Catálogo de Inventario" }));
    expect(screen.getByRole("heading", { name: "Catálogo de Inventario" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Reactivos" }));
    expect(screen.getByRole("heading", { name: "Perfiles de Reactivos" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Lotes" }));
    expect(screen.getByRole("heading", { name: "Lotes de Inventario" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Compras" }));
    expect(screen.getByRole("heading", { name: "Órdenes de Compra" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Movimientos de Stock" }));
    expect(screen.getByRole("heading", { name: "Movimientos de Inventario" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Ajustes de Inventario" }));
    expect(screen.getByRole("heading", { name: "Ajustes de Inventario" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Mermas" }));
    expect(screen.getByRole("heading", { name: "Mermas y Disposición" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Control de Calidad Interno" }));
    expect(
      screen.getByRole("heading", { name: "Controles Internos de Calidad" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Equipos" }));
    expect(screen.getByRole("heading", { name: "Equipos" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Calibraciones" }));
    expect(screen.getByRole("heading", { name: "Calibraciones" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Mantenimiento" }));
    expect(screen.getByRole("heading", { name: "Mantenimiento" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Contenido Público" }));
    expect(
      screen.getByRole("heading", { name: "Revisión de Contenido Público" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Citas Públicas" }));
    expect(
      screen.getByRole("heading", { name: "Solicitudes Públicas de Cita" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cotizaciones Públicas" }));
    expect(
      screen.getByRole("heading", { name: "Solicitudes Públicas de Cotización" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Controles de Calidad Ext." }));
    expect(
      screen.getByRole("heading", { name: "Controles de Calidad Externos" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Gestión CAPA" }));
    expect(screen.getByRole("heading", { name: "Gestión de CAPA" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Auditorías de Calidad" }));
    expect(
      screen.getByRole("heading", { name: "Gestión de Auditorías de Calidad" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Evidencia de Cumplimiento" }));
    expect(screen.getByRole("heading", { name: "Evidencia de Cumplimiento" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Eventos de Calidad" }));
    expect(
      screen.getByRole("heading", { name: "Ingesta de Eventos de Calidad" }),
    ).toBeInTheDocument();
  });

  it("navigates to Product Marketplace and Entitlements screens (COM-MOD-017)", async () => {
    const user = userEvent.setup();
    render(<App />);

    await user.click(screen.getByRole("button", { name: "Catálogo de Marketplace" }));
    expect(
      screen.getByRole("heading", { name: "Catálogo de Paquetes de Marketplace" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Ofertas Comerciales" }));
    expect(screen.getByRole("heading", { name: "Ofertas Comerciales" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Derechos de Tenant" }));
    expect(screen.getByRole("heading", { name: "Derechos de Tenant" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Instalaciones de Paquetes" }));
    expect(screen.getByRole("heading", { name: "Instalaciones de Paquetes" })).toBeInTheDocument();
  });

  it("navigates to Imaging Operations screens (COM-MOD-014)", async () => {
    const user = userEvent.setup();
    render(<App />);

    await user.click(screen.getByRole("button", { name: "Citas de Imagen" }));
    expect(screen.getByRole("heading", { name: "Citas de Imagenología" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Recepción de Imagen" }));
    expect(
      screen.getByRole("heading", { name: "Recepción e Ingesta de Imagenología" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Estudios de Imagen" }));
    expect(
      screen.getByRole("heading", { name: "Gestión de Estudios de Imagenología" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Config. DICOM" }));
    expect(
      screen.getByRole("heading", { name: "Configuración e Integración DICOM" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Endpoints PACS" }));
    expect(
      screen.getByRole("heading", { name: "Puente de Integración PACS / WADO / STOW" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Dictado Médico" }));
    expect(
      screen.getByRole("heading", { name: "Dictado Médico y Transcripción" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Reportes de Radiología" }));
    expect(
      screen.getByRole("heading", { name: "Firma y Emisión de Reportes Radiológicos" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Entrega de Estudios" }));
    expect(
      screen.getByRole("heading", { name: "Paquetes y Entrega de Estudios" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Asistente AI" }));
    expect(
      screen.getByRole("heading", { name: "Asistente AI y Revisión Humana" }),
    ).toBeInTheDocument();
  });

  it("only renders tabs the current session has permission for", () => {
    render(<App />);

    // The local dev fixture session defaults to ADMIN, which is granted every screen
    // permission, so all 65 navigation tabs remain visible (62 prior + HOP-HARD-FE-001
    // Appointments/Admissions/Quotations).
    const nav = screen.getByRole("navigation", { name: "Pantallas de administración" });
    expect(within(nav).getAllByRole("button")).toHaveLength(65);
  });
});
