import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import App from "./App";
import { ApiError } from "./api/httpClient";

const listReferredOrdersMock = vi.fn();
const getPatientHistoryAsDoctorMock = vi.fn();
const getResultNotificationsMock = vi.fn();

vi.mock("./api/diagnosticOrdersApi", () => ({
  listReferredOrders: (...args: unknown[]) => listReferredOrdersMock(...args),
}));

vi.mock("./api/patientResultHistoryApi", () => ({
  getPatientHistoryAsDoctor: (...args: unknown[]) => getPatientHistoryAsDoctorMock(...args),
}));

vi.mock("./api/resultNotificationsApi", () => ({
  getResultNotifications: (...args: unknown[]) => getResultNotificationsMock(...args),
}));

const ORDER_ADA = {
  orderId: "ord-101",
  tenantId: "tenant-local",
  laboratoryId: "lab-01",
  branchId: "branch-north",
  intakeChannel: "walk_in",
  patientSnapshot: {
    patientId: "Patient-A",
    sourceVersion: 1,
    fullName: "Ada Lovelace",
    documentType: "national_id",
    documentNumberMasked: "***1234",
    birthDate: "1990-01-01",
    capturedAt: "2026-07-01T00:00:00Z",
  },
  doctorSnapshot: {
    doctorId: "Doctor-01",
    sourceVersion: 1,
    fullName: "Dr. Grace Hopper",
    licenseNumber: "LIC-1",
    capturedAt: "2026-07-01T00:00:00Z",
  },
  branchSnapshot: {
    branchId: "branch-north",
    sourceVersion: 1,
    name: "North Branch",
    capturedAt: "2026-07-01T00:00:00Z",
  },
  status: "accepted",
  version: 1,
  createdAt: "2026-07-01T00:00:00Z",
};

const ORDER_MARIE = {
  ...ORDER_ADA,
  orderId: "ord-102",
  patientSnapshot: {
    ...ORDER_ADA.patientSnapshot,
    patientId: "Patient-M",
    fullName: "Marie Curie",
  },
  status: "completed",
};

// Mock window.fetch for the real login flow / mock session persistence
const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("Doctor Portal App", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
    mockFetch.mockReset();
    listReferredOrdersMock.mockReset().mockResolvedValue([ORDER_ADA, ORDER_MARIE]);
    getPatientHistoryAsDoctorMock.mockReset().mockResolvedValue({
      patientId: "Patient-A",
      entries: [
        {
          resultId: "res-301",
          analyteName: "Glucose",
          stringValue: "140 mg/dL",
          referenceRange: "70-100 mg/dL",
          isAbnormal: true,
          releasedAt: "2026-07-19T10:00:00Z",
        },
      ],
    });
    getResultNotificationsMock.mockReset().mockResolvedValue([
      {
        notificationRequestId: "notif-901",
        resultId: "res-301",
        tenantId: "tenant-local",
        recipientType: "doctor",
        recipientId: "Doctor-01",
        channel: "email",
        status: "delivered",
        dispatchedAt: "2026-07-19T10:05:00Z",
        createdAt: "2026-07-19T10:05:00Z",
      },
    ]);
  });

  it("renders login form by default and allows language switching", () => {
    render(<App />);
    expect(screen.getByText("Iniciar Sesión")).toBeInTheDocument();
    expect(screen.getByText("HOP Portal Médico")).toBeInTheDocument();

    fireEvent.click(screen.getByText("EN"));
    expect(screen.getAllByText("Sign In")[0]).toBeInTheDocument();
    expect(screen.getByText("HOP Doctor Portal")).toBeInTheDocument();

    fireEvent.click(screen.getByText("ES"));
    expect(screen.getByText("Iniciar Sesión")).toBeInTheDocument();
  });

  it("handles real credential login success", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ token: "local-session:tenant-local:Doctor-01" }),
    });
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ givenName: "Grace", familyName: "Hopper" }),
    });

    render(<App />);
    fireEvent.click(screen.getByRole("button", { name: "Ingresar" }));

    await waitFor(() => {
      expect(screen.getByText("Grace Hopper")).toBeInTheDocument();
    });
  });

  it("handles login failures (invalid and locked accounts)", async () => {
    mockFetch.mockResolvedValueOnce({ ok: false, status: 401 });

    render(<App />);
    fireEvent.click(screen.getByRole("button", { name: "Ingresar" }));

    await waitFor(() => {
      expect(screen.getByText("Credenciales incorrectas.")).toBeInTheDocument();
    });

    mockFetch.mockResolvedValueOnce({ ok: false, status: 403 });
    fireEvent.click(screen.getByRole("button", { name: "Ingresar" }));

    await waitFor(() => {
      expect(screen.getByText("Cuenta suspendida. Contacta a soporte.")).toBeInTheDocument();
    });
  });

  it("logs in with mock developer account, navigates every tab and logs out", async () => {
    render(<App />);

    fireEvent.click(screen.getByText("Dr. Grace Hopper"));

    await waitFor(() => {
      expect(screen.getByText("Dr. Grace Hopper")).toBeInTheDocument();
    });

    // Dynamic, permission-filtered nav: REFERRING_DOCTOR holds all 4 doctor-portal permissions.
    expect(screen.getByText("Mis Pacientes")).toBeInTheDocument();
    expect(screen.getByText("Resultados")).toBeInTheDocument();
    expect(screen.getByText("Mis Órdenes")).toBeInTheDocument();
    expect(screen.getByText("Notificaciones")).toBeInTheDocument();

    // PATIENTS TAB (default): two distinct referred patients derived from server-filtered orders.
    await waitFor(() => {
      expect(screen.getByText("Ada Lovelace")).toBeInTheDocument();
      expect(screen.getByText("Marie Curie")).toBeInTheDocument();
    });

    // ORDERS TAB
    fireEvent.click(screen.getByText("Mis Órdenes"));
    await waitFor(() => {
      expect(screen.getByText("ACCEPTED")).toBeInTheDocument();
      expect(screen.getByText("COMPLETED")).toBeInTheDocument();
    });

    // RESULTS TAB via patient selector
    fireEvent.click(screen.getByText("Resultados"));
    expect(screen.getByText("Selecciona un paciente primero.")).toBeInTheDocument();
    fireEvent.change(screen.getByLabelText("Selecciona un paciente"), {
      target: { value: "Patient-A" },
    });
    await waitFor(() => {
      expect(screen.getByText("Glucose")).toBeInTheDocument();
      expect(screen.getByText(/140 mg\/dL/)).toBeInTheDocument();
    });
    expect(getPatientHistoryAsDoctorMock).toHaveBeenCalledWith(
      "Patient-A",
      "tenant-local",
      "Doctor-01",
    );

    // NOTIFICATIONS TAB reuses the same patient selector pattern
    fireEvent.click(screen.getByText("Notificaciones"));
    fireEvent.change(screen.getByLabelText("Selecciona un paciente"), {
      target: { value: "Patient-A" },
    });
    await waitFor(() => {
      expect(screen.getByText("EMAIL")).toBeInTheDocument();
      expect(screen.getByText("DELIVERED")).toBeInTheDocument();
    });

    // "View Results" shortcut from the Patients tab jumps straight to Results pre-selected.
    fireEvent.click(screen.getByText("Mis Pacientes"));
    await waitFor(() => expect(screen.getByText("Ada Lovelace")).toBeInTheDocument());
    fireEvent.click(screen.getAllByText("Ver Resultados")[0]);
    await waitFor(() => {
      expect(screen.getByText("Glucose")).toBeInTheDocument();
    });

    // LOG OUT
    fireEvent.click(screen.getByText("Cerrar Sesión"));
    expect(screen.getByText("Iniciar Sesión")).toBeInTheDocument();
  });

  it("shows the empty-state hint when the doctor has no referred patients", async () => {
    listReferredOrdersMock.mockResolvedValue([]);
    render(<App />);
    fireEvent.click(screen.getByText("Dr. Grace Hopper"));

    await waitFor(() => {
      expect(screen.getByText("No se encontraron registros.")).toBeInTheDocument();
      expect(
        screen.getByText("Aún no tienes pacientes referidos en esta organización."),
      ).toBeInTheDocument();
    });
  });

  it("shows a generic error state and, on 401, forces the doctor back to the login screen", async () => {
    listReferredOrdersMock.mockRejectedValue(new ApiError(401, "AUTHENTICATION_REQUIRED"));
    render(<App />);
    fireEvent.click(screen.getByText("Dr. Grace Hopper"));

    await waitFor(() => {
      expect(screen.getByText("Iniciar Sesión")).toBeInTheDocument();
    });
  });

  it("surfaces a permission-denied state when the results history call is forbidden", async () => {
    getPatientHistoryAsDoctorMock.mockRejectedValue(
      new ApiError(403, "DELIVERY_DOCTOR_REFERRAL_MISMATCH"),
    );
    render(<App />);
    fireEvent.click(screen.getByText("Dr. Grace Hopper"));

    await waitFor(() => expect(screen.getByText("Ada Lovelace")).toBeInTheDocument());
    fireEvent.click(screen.getByText("Resultados"));
    fireEvent.change(screen.getByLabelText("Selecciona un paciente"), {
      target: { value: "Patient-A" },
    });

    await waitFor(() => {
      expect(
        screen.getByText("No tienes permiso para acceder a esta sección."),
      ).toBeInTheDocument();
    });
  });
});
