import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { ImagingAppointmentsScreen } from "../components/screens/ImagingAppointmentsScreen";
import { ImagingReceptionScreen } from "../components/screens/ImagingReceptionScreen";
import { ImagingStudiesScreen } from "../components/screens/ImagingStudiesScreen";
import { ImagingDicomScreen } from "../components/screens/ImagingDicomScreen";
import { ImagingPacsScreen } from "../components/screens/ImagingPacsScreen";
import { ImagingDictationScreen } from "../components/screens/ImagingDictationScreen";
import { ImagingReportsScreen } from "../components/screens/ImagingReportsScreen";
import { ImagingDeliveryScreen } from "../components/screens/ImagingDeliveryScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider } from "../state/AdminScopeContext";
import * as api from "../api/imagingOperationsApi";

function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>{children}</AdminScopeProvider>
    </LocaleProvider>
  );
}

describe("Imaging Operations Screens", () => {
  it("renders ImagingAppointmentsScreen and schedules a slot", async () => {
    vi.spyOn(api, "scheduleAppointmentSlot").mockResolvedValue({
      id: "SLOT-1",
      tenantId: "TEN-1",
      patientId: "PAT-1",
      branchId: "BR-1",
      modality: "CT",
      procedureCode: "CT-CHEST",
      procedureRoomId: "RM-1",
      startTime: "2026-07-25T10:00:00Z",
      durationMinutes: 30,
      status: "SCHEDULED",
    });

    render(
      <Harness>
        <ImagingAppointmentsScreen />
      </Harness>
    );

    expect(screen.getByText("Citas de Imagenología")).toBeInTheDocument();

    const patientInput = screen.getByLabelText("ID de Paciente");
    fireEvent.change(patientInput, { target: { value: "PAT-1" } });

    const scheduleBtn = screen.getByRole("button", { name: "Programar Cita" });
    fireEvent.click(scheduleBtn);

    await waitFor(() => {
      expect(api.scheduleAppointmentSlot).toHaveBeenCalled();
    });
  });

  it("renders ImagingReceptionScreen and performs check-in", async () => {
    vi.spyOn(api, "checkInReception").mockResolvedValue({
      id: "INTAKE-1",
      tenantId: "TEN-1",
      appointmentSlotId: "SLOT-1",
      patientId: "PAT-1",
      preparationVerified: true,
      status: "CHECKED_IN",
    });

    render(
      <Harness>
        <ImagingReceptionScreen />
      </Harness>
    );

    expect(screen.getByText("Recepción e Ingesta de Imagenología")).toBeInTheDocument();

    const slotInput = screen.getByLabelText("ID de Cita");
    const patientInput = screen.getByLabelText("ID de Paciente");

    fireEvent.change(slotInput, { target: { value: "SLOT-1" } });
    fireEvent.change(patientInput, { target: { value: "PAT-1" } });

    const checkInBtn = screen.getByRole("button", { name: "Registrar Recepción (Check-in)" });
    fireEvent.click(checkInBtn);

    await waitFor(() => {
      expect(api.checkInReception).toHaveBeenCalled();
    });
  });

  it("renders ImagingStudiesScreen and creates a study", async () => {
    vi.spyOn(api, "createStudy").mockResolvedValue({
      id: "STUDY-1",
      tenantId: "TEN-1",
      accessionNumber: "ACC-1",
      patientId: "PAT-1",
      modality: "CT",
      seriesCount: 0,
      instanceCount: 0,
      status: "CREATED",
    });

    render(
      <Harness>
        <ImagingStudiesScreen />
      </Harness>
    );

    expect(screen.getByText("Gestión de Estudios de Imagenología")).toBeInTheDocument();

    const patientInput = screen.getByLabelText("ID de Paciente");
    fireEvent.change(patientInput, { target: { value: "PAT-1" } });

    const createBtn = screen.getByRole("button", { name: "Crear Estudio" });
    fireEvent.click(createBtn);

    await waitFor(() => {
      expect(api.createStudy).toHaveBeenCalled();
    });
  });

  it("renders ImagingDicomScreen and registers DICOM config", async () => {
    vi.spyOn(api, "registerDicomConfig").mockResolvedValue({
      id: "CFG-1",
      tenantId: "TEN-1",
      aeTitle: "PACS",
      host: "127.0.0.1",
      port: 104,
      modalityType: "CT",
      status: "REGISTERED",
    });

    render(
      <Harness>
        <ImagingDicomScreen />
      </Harness>
    );

    expect(screen.getByText("Configuración e Integración DICOM")).toBeInTheDocument();

    const regBtn = screen.getByRole("button", { name: "Registrar Nodo DICOM" });
    fireEvent.click(regBtn);

    await waitFor(() => {
      expect(api.registerDicomConfig).toHaveBeenCalled();
    });
  });

  it("renders ImagingPacsScreen and registers PACS endpoint", async () => {
    vi.spyOn(api, "registerPacsEndpoint").mockResolvedValue({
      id: "PACS-1",
      tenantId: "TEN-1",
      pacsNodeId: "NODE-1",
      baseUrl: "https://pacs.org",
      protocol: "DICOM_WEB",
      status: "REGISTERED",
    });

    render(
      <Harness>
        <ImagingPacsScreen />
      </Harness>
    );

    expect(screen.getByText("Puente de Integración PACS / WADO / STOW")).toBeInTheDocument();

    const regBtn = screen.getByRole("button", { name: "Registrar Endpoint PACS" });
    fireEvent.click(regBtn);

    await waitFor(() => {
      expect(api.registerPacsEndpoint).toHaveBeenCalled();
    });
  });

  it("renders ImagingDictationScreen and creates dictation", async () => {
    vi.spyOn(api, "createDictation").mockResolvedValue({
      id: "DICT-1",
      tenantId: "TEN-1",
      studyId: "STUDY-1",
      dictationText: "Normal findings",
      status: "DRAFT",
    });

    render(
      <Harness>
        <ImagingDictationScreen />
      </Harness>
    );

    expect(screen.getByText("Dictado Médico y Transcripción")).toBeInTheDocument();

    const studyInput = screen.getByLabelText("ID de Estudio");
    fireEvent.change(studyInput, { target: { value: "STUDY-1" } });

    const createBtn = screen.getByRole("button", { name: "Registrar Dictado" });
    fireEvent.click(createBtn);

    await waitFor(() => {
      expect(api.createDictation).toHaveBeenCalled();
    });
  });

  it("renders ImagingReportsScreen and creates draft report", async () => {
    vi.spyOn(api, "createRadiologyReport").mockResolvedValue({
      id: "REP-1",
      tenantId: "TEN-1",
      studyId: "STUDY-1",
      findingsText: "Findings",
      impressionText: "Impression",
      signed: false,
    });

    render(
      <Harness>
        <ImagingReportsScreen />
      </Harness>
    );

    expect(screen.getByText("Firma y Emisión de Reportes Radiológicos")).toBeInTheDocument();

    const studyInput = screen.getByLabelText("ID de Estudio");
    fireEvent.change(studyInput, { target: { value: "STUDY-1" } });

    const createBtn = screen.getByRole("button", { name: "Crear Borrador de Reporte" });
    fireEvent.click(createBtn);

    await waitFor(() => {
      expect(api.createRadiologyReport).toHaveBeenCalled();
    });
  });

  it("renders ImagingDeliveryScreen and creates delivery package", async () => {
    vi.spyOn(api, "createDeliveryPackage").mockResolvedValue({
      id: "DEL-1",
      tenantId: "TEN-1",
      studyId: "STUDY-1",
      patientId: "PAT-1",
      deliveryFormat: "PDF",
      status: "PREPARED",
    });

    render(
      <Harness>
        <ImagingDeliveryScreen />
      </Harness>
    );

    expect(screen.getByText("Paquetes y Entrega de Estudios")).toBeInTheDocument();

    const studyInput = screen.getByLabelText("ID de Estudio");
    const patientInput = screen.getByLabelText("ID de Paciente");

    fireEvent.change(studyInput, { target: { value: "STUDY-1" } });
    fireEvent.change(patientInput, { target: { value: "PAT-1" } });

    const createBtn = screen.getByRole("button", { name: "Crear Paquete de Entrega" });
    fireEvent.click(createBtn);

    await waitFor(() => {
      expect(api.createDeliveryPackage).toHaveBeenCalled();
    });
  });
});
