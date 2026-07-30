import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import App from "./App";

vi.mock("./api/patientResultHistoryApi", () => ({
  getPatientHistory: vi.fn().mockResolvedValue({
    patientId: "Patient-01",
    entries: [
      {
        resultId: "res-301",
        analyteName: "Glucose",
        stringValue: "140 mg/dL",
        referenceRange: "70-100 mg/dL",
        isAbnormal: true,
        releasedAt: "2026-07-19T10:00:00Z",
      },
      {
        resultId: "res-302",
        analyteName: "Cholesterol",
        stringValue: "180 mg/dL",
        referenceRange: "<200 mg/dL",
        isAbnormal: false,
        releasedAt: "2026-07-19T10:00:00Z",
      },
    ],
  }),
}));

const getMyImagingDeliveryPackagesMock = vi.fn();
const getMyImagingReportsForStudyMock = vi.fn();

vi.mock("./api/imagingDeliveryApi", () => ({
  getMyImagingDeliveryPackages: (...args: unknown[]) => getMyImagingDeliveryPackagesMock(...args),
  getMyImagingReportsForStudy: (...args: unknown[]) => getMyImagingReportsForStudyMock(...args),
}));

// Mock window.fetch
const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("Patient Portal App", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
    mockFetch.mockReset();
    getMyImagingDeliveryPackagesMock.mockReset().mockResolvedValue([
      {
        packageId: "pkg-1",
        tenantId: "tenant-local",
        studyId: "std-1",
        patientId: "Patient-01",
        deliveryFormat: "DICOM_ZIP",
        deliveryStatus: "DELIVERED",
      },
    ]);
    getMyImagingReportsForStudyMock.mockReset().mockResolvedValue([
      {
        reportId: "rep-1",
        studyId: "std-1",
        reportStatus: "FINAL_SIGNED",
        findingsText: "No acute findings.",
        impressionText: "Unremarkable study.",
      },
    ]);
  });

  it("renders login form by default and allows language switching", () => {
    render(<App />);
    expect(screen.getByText("Iniciar Sesión")).toBeInTheDocument();
    expect(screen.getByText("HOP Portal de Pacientes")).toBeInTheDocument();

    // Switch to English
    fireEvent.click(screen.getByText("EN"));
    expect(screen.getAllByText("Sign In")[0]).toBeInTheDocument();
    expect(screen.getByText("HOP Patient Portal")).toBeInTheDocument();

    // Switch back to Spanish
    fireEvent.click(screen.getByText("ES"));
    expect(screen.getByText("Iniciar Sesión")).toBeInTheDocument();
  });

  it("handles real credential login success", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ token: "local-session:tenant-local:Patient-01" }),
    });

    // Mock patient profile get
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({
        patientCode: "PAT-01",
        givenName: "John",
        familyName: "Doe",
        birthDate: "1990-05-15",
        sexAtBirth: "male",
        primaryDocumentType: "National ID",
        primaryDocumentNumber: "MX-1234",
        addressStreet: "Main St",
        addressCity: "CDMX",
        addressPostalCode: "01000",
        emergencyContacts: [],
      }),
    });

    render(<App />);

    // Trigger form submit
    fireEvent.click(screen.getByRole("button", { name: "Ingresar" }));

    await waitFor(() => {
      expect(screen.getByText("Bienvenido/a,")).toBeInTheDocument();
      expect(screen.getByText("John Doe")).toBeInTheDocument();
    });
  });

  it("handles login failures (invalid and locked accounts)", async () => {
    // 401 Unauthorized
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 401,
    });

    render(<App />);
    fireEvent.click(screen.getByRole("button", { name: "Ingresar" }));

    await waitFor(() => {
      expect(screen.getByText("Credenciales incorrectas.")).toBeInTheDocument();
    });

    // 403 Forbidden
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 403,
    });

    fireEvent.click(screen.getByRole("button", { name: "Ingresar" }));

    await waitFor(() => {
      expect(screen.getByText("Cuenta suspendida. Contacta a soporte.")).toBeInTheDocument();
    });
  });

  it("logs in with mock developer account and displays tabs correctly", async () => {
    render(<App />);

    // Click on mock user "John Doe (Patient)"
    fireEvent.click(screen.getByText("John Doe (Patient)"));

    // Verify shell and welcome message
    expect(screen.getByText("Bienvenido/a,")).toBeInTheDocument();
    expect(screen.getByText("John Doe (Patient)")).toBeInTheDocument();

    // Verify tabs are present
    expect(screen.getByText("Mi Perfil")).toBeInTheDocument();
    expect(screen.getByText("Resultados Médicos")).toBeInTheDocument();
    expect(screen.getByText("Mis Citas")).toBeInTheDocument();
    expect(screen.getByText("Mis Órdenes")).toBeInTheDocument();
    expect(screen.getByText("Notificaciones")).toBeInTheDocument();
    expect(screen.getByText("Imágenes")).toBeInTheDocument();

    // PROFILE TAB (Default tab)
    // Check emergency contacts sub-header or field labels
    await waitFor(() => {
      expect(screen.getByText("Contactos de Emergencia")).toBeInTheDocument();
    });

    // RESULTS TAB
    fireEvent.click(screen.getByText("Resultados Médicos"));
    await waitFor(() => {
      expect(screen.getByText("Glucose")).toBeInTheDocument();
      expect(screen.getByText("Cholesterol")).toBeInTheDocument();
      expect(screen.getByText("140 mg/dL (Abnormal)")).toBeInTheDocument();
      expect(screen.getByText("180 mg/dL")).toBeInTheDocument();
    });

    // APPOINTMENTS TAB
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => [
        {
          appointmentId: "apt-live",
          tenantId: "tenant-local",
          laboratoryId: "lab-01",
          branchId: "branch-live",
          patientId: "Patient-01",
          doctorId: "Doctor-01",
          scheduledStart: "2026-07-20T10:00:00Z",
          scheduledEnd: "2026-07-20T10:30:00Z",
          channel: "PORTAL",
          status: "confirmed",
        },
      ],
    });

    fireEvent.click(screen.getByText("Mis Citas"));
    await waitFor(() => {
      expect(screen.getByText("CONFIRMED")).toBeInTheDocument();
    });

    // ORDERS TAB
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => [
        {
          orderId: "ord-live",
          tenantId: "tenant-local",
          laboratoryId: "lab-01",
          branchId: "branch-live",
          intakeChannel: "PORTAL",
          patientId: "Patient-01",
          doctorId: "Doctor-01",
          status: "accepted",
          lines: [{ testDefinitionId: "Hemoglobin", catalogItemKind: "test", quantity: 1 }],
        },
      ],
    });

    fireEvent.click(screen.getByText("Mis Órdenes"));
    await waitFor(() => {
      expect(screen.getByText("ACCEPTED")).toBeInTheDocument();
    });

    // NOTIFICATIONS TAB
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => [
        {
          notificationRequestId: "live-notif",
          resultId: "res-301",
          tenantId: "tenant-local",
          recipientType: "patient",
          recipientId: "Patient-01",
          channel: "sms",
          status: "delivered",
          dispatchedAt: "2026-07-19T10:05:00Z",
          createdAt: "2026-07-19T10:05:00Z",
        },
      ],
    });

    fireEvent.click(screen.getByText("Notificaciones"));
    await waitFor(() => {
      expect(screen.getAllByText("DELIVERED")[0]).toBeInTheDocument();
    });

    // IMAGING TAB
    fireEvent.click(screen.getByText("Imágenes"));
    await waitFor(() => {
      expect(screen.getByText("No acute findings.")).toBeInTheDocument();
      expect(screen.getByText("Unremarkable study.")).toBeInTheDocument();
    });
    expect(getMyImagingDeliveryPackagesMock).toHaveBeenCalledWith("Patient-01");
    expect(getMyImagingReportsForStudyMock).toHaveBeenCalledWith("std-1", "Patient-01");

    // LOG OUT
    fireEvent.click(screen.getByText("Cerrar Sesión"));
    expect(screen.getByText("Iniciar Sesión")).toBeInTheDocument();
  });
});
