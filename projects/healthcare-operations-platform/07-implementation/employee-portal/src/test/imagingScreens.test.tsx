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
      </Harness>,
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
      </Harness>,
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
      </Harness>,
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
      </Harness>,
    );

    expect(screen.getByText("Configuración e Integración DICOM")).toBeInTheDocument();

    // Host field must be filled; its default is now empty (hardcoded IP removed by QA lint fix)
    const hostInput = screen.getByLabelText("Host / IP");
    fireEvent.change(hostInput, { target: { value: "dicom.hospital.local" } });

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
      </Harness>,
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
      </Harness>,
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
      </Harness>,
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
      </Harness>,
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

  it("loads radiology reports, selects one and signs it", async () => {
    const draftReport: api.RadiologyReport = {
      id: "REP-SIGN-1",
      tenantId: "TEN-1",
      studyId: "STUDY-SIGN-1",
      findingsText: "No focal infiltrate.",
      impressionText: "No acute findings.",
      signed: false,
    };

    const signedReport: api.RadiologyReport = {
      ...draftReport,
      signed: true,
      signedBy: "RAD-1",
      signedAt: "2026-07-25T20:10:00Z",
    };

    vi.spyOn(api, "listRadiologyReportsForStudy").mockResolvedValue([draftReport]);
    vi.spyOn(api, "signRadiologyReport").mockResolvedValue(signedReport);

    render(
      <Harness>
        <ImagingReportsScreen />
      </Harness>,
    );

    const searchInput = document.querySelector<HTMLInputElement>("#rep-search-study-input");
    expect(searchInput).not.toBeNull();
    fireEvent.change(searchInput!, { target: { value: "STUDY-SIGN-1" } });

    const loadBtn = screen.getByRole("button", { name: "Cargar" });
    fireEvent.click(loadBtn);

    const row = await screen.findByRole("button", { name: "REP-SIGN-1" });
    fireEvent.click(row);

    const signBtn = await screen.findByRole("button", { name: "Firmar Reporte" });
    fireEvent.click(signBtn);

    await waitFor(() => {
      expect(api.signRadiologyReport).toHaveBeenCalledWith("REP-SIGN-1");
    });
  });

  it("loads delivery packages, selects one and marks it delivered", async () => {
    const preparedPackage: api.ImagingDeliveryPackage = {
      id: "DEL-MARK-1",
      tenantId: "TEN-1",
      studyId: "STUDY-DEL-1",
      patientId: "PAT-DEL-1",
      deliveryFormat: "DICOM_PORTAL_PDF",
      status: "PREPARED",
    };

    const deliveredPackage: api.ImagingDeliveryPackage = {
      ...preparedPackage,
      status: "DELIVERED",
      deliveredBy: "USR-1",
      deliveredAt: "2026-07-25T20:11:00Z",
    };

    vi.spyOn(api, "listDeliveryPackagesForPatient").mockResolvedValue([preparedPackage]);
    vi.spyOn(api, "markDeliveryPackageDelivered").mockResolvedValue(deliveredPackage);

    render(
      <Harness>
        <ImagingDeliveryScreen />
      </Harness>,
    );

    const searchInput = document.querySelector<HTMLInputElement>("#del-search-patient-input");
    expect(searchInput).not.toBeNull();
    fireEvent.change(searchInput!, { target: { value: "PAT-DEL-1" } });

    const loadBtn = screen.getByRole("button", { name: "Cargar" });
    fireEvent.click(loadBtn);

    const row = await screen.findByRole("button", { name: "DEL-MARK-1" });
    fireEvent.click(row);

    const deliverBtn = await screen.findByRole("button", { name: "Marcar como Entregado" });
    fireEvent.click(deliverBtn);

    await waitFor(() => {
      expect(api.markDeliveryPackageDelivered).toHaveBeenCalledWith("DEL-MARK-1");
    });
  });

  it("loads DICOM configs, selects one and runs C-ECHO and worklist queries", async () => {
    const mockConfig: api.DicomAdapterConfiguration = {
      id: "CFG-ECHO-1",
      tenantId: "TEN-1",
      aeTitle: "PACS_ECHO",
      host: "dicom.hospital.local",
      port: 104,
      modalityType: "MR",
      status: "REGISTERED",
    };

    vi.spyOn(api, "listDicomConfigs").mockResolvedValue([mockConfig]);
    vi.spyOn(api, "echoCEcho").mockResolvedValue({ result: "ECHO OK" });
    vi.spyOn(api, "queryDicomWorklist").mockResolvedValue([
      {
        patientId: "PAT-WL-1",
        patientName: "Test Patient",
        accessionNumber: "ACC-WL-1",
        modality: "MR",
        scheduledProcedureStepId: "SPS-1",
        scheduledDate: "2026-07-25",
      },
    ]);

    render(
      <Harness>
        <ImagingDicomScreen />
      </Harness>,
    );

    // Load configs
    const loadBtn = screen.getByRole("button", { name: "Cargar" });
    fireEvent.click(loadBtn);

    // Wait for the config row to appear in the table (state update after async mock)
    const row = await screen.findByRole("button", { name: "CFG-ECHO-1" });
    fireEvent.click(row);

    // Run C-ECHO - button appears after row selection sets selectedConfig state
    const echoBtn = await screen.findByRole("button", { name: "Probar C-ECHO" });
    fireEvent.click(echoBtn);

    await waitFor(() => {
      expect(api.echoCEcho).toHaveBeenCalledWith("CFG-ECHO-1");
    });

    // Run worklist
    const wlBtn = screen.getByRole("button", { name: "Consultar Worklist (C-FIND)" });
    fireEvent.click(wlBtn);

    await waitFor(() => {
      expect(api.queryDicomWorklist).toHaveBeenCalledWith("CFG-ECHO-1");
    });
  });

  it("loads PACS endpoints, selects one and runs QIDO and WADO queries", async () => {
    const mockEndpoint: api.PacsIntegrationEndpoint = {
      id: "PACS-SEL-1",
      tenantId: "TEN-1",
      pacsNodeId: "NODE-SEL-1",
      baseUrl: "https://pacs.hospital.local",
      protocol: "DICOM_WEB",
      status: "REGISTERED",
    };

    vi.spyOn(api, "listPacsEndpoints").mockResolvedValue([mockEndpoint]);
    vi.spyOn(api, "qidoSearchPacsStudies").mockResolvedValue([
      {
        studyInstanceUid: "1.2.3.4.5",
        patientId: "PAT-Q-1",
        patientName: "Query Patient",
        studyDate: "2026-07-25",
        modality: "CT",
        numberOfStudyRelatedInstances: 10,
      },
    ]);
    vi.spyOn(api, "getPacsWadoUrl").mockResolvedValue({
      studyInstanceUid: "1.2.3.4.5",
      wadoUrl: "https://pacs.hospital.local/wado/1.2.3.4.5",
      contentType: "application/dicom",
    });

    render(
      <Harness>
        <ImagingPacsScreen />
      </Harness>,
    );

    // Load endpoints
    const loadBtn = screen.getByRole("button", { name: "Cargar" });
    fireEvent.click(loadBtn);

    await waitFor(() => {
      expect(api.listPacsEndpoints).toHaveBeenCalled();
    });

    // Select the loaded endpoint row - click the ID link-button rendered by DataTable
    const row = await screen.findByRole("button", { name: "PACS-SEL-1" });
    fireEvent.click(row);

    // Run QIDO search - button appears after row selection
    const qidoBtn = await screen.findByRole("button", { name: "Búsqueda QIDO-RS" });
    fireEvent.click(qidoBtn);

    await waitFor(() => {
      expect(api.qidoSearchPacsStudies).toHaveBeenCalledWith("PACS-SEL-1");
    });

    // Run WADO retrieve URL
    const wadoBtn = screen.getByRole("button", { name: "Obtener URL WADO-RS" });
    fireEvent.click(wadoBtn);

    await waitFor(() => {
      expect(api.getPacsWadoUrl).toHaveBeenCalledWith("PACS-SEL-1", expect.any(String));
    });
  });
});
