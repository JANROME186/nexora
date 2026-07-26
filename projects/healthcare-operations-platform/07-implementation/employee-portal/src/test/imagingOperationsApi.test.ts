import { afterEach, describe, expect, it, vi } from "vitest";
import {
  checkInReception,
  createDeliveryPackage,
  createDictation,
  createRadiologyReport,
  createStudy,
  echoCEcho,
  getAppointmentSlot,
  getDeliveryPackage,
  getDictation,
  getDicomConfig,
  getPacsEndpoint,
  getPacsWadoUrl,
  getRadiologyReport,
  getReceptionIntake,
  getReceptionIntakeBySlot,
  getStudy,
  listAppointmentSlotsForPatient,
  listDeliveryPackagesForPatient,
  listDictationsForStudy,
  listDicomConfigs,
  listPacsEndpoints,
  listRadiologyReportsForStudy,
  listStudiesForPatient,
  markDeliveryPackageDelivered,
  qidoSearchPacsStudies,
  queryDicomWorklist,
  queryPacsStudy,
  registerDicomConfig,
  registerPacsEndpoint,
  requestDicomTransfer,
  scheduleAppointmentSlot,
  signRadiologyReport,
  stowStorePacs,
  updateAppointmentSlotStatus,
  updateStudyStatus,
  validateDicomHeader,
} from "../api/imagingOperationsApi";

function mockFetchOnce(response: Partial<Response> & { jsonBody?: unknown }) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok: response.ok ?? true,
    status: response.status ?? 200,
    statusText: response.statusText ?? "OK",
    json: async () => response.jsonBody,
  } as Response);
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

describe("imagingOperationsApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    window.localStorage.clear();
  });

  // BCM-IMG-001: Appointments
  it("schedules an appointment slot via POST", async () => {
    const fetchMock = mockFetchOnce({ status: 200, jsonBody: { id: "SLOT-1" } });
    const res = await scheduleAppointmentSlot({
      patientId: "PAT-1",
      branchId: "BR-1",
      modality: "CT",
      procedureCode: "PROC-1",
      procedureRoomId: "RM-1",
      startTime: "2026-07-25T10:00:00Z",
      durationMinutes: 30,
      notes: "Test notes",
    });
    expect(res).toEqual({ id: "SLOT-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/imaging/appointments",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("fetches single appointment slot", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { id: "SLOT-1" } });
    const res = await getAppointmentSlot("SLOT-1");
    expect(res).toEqual({ id: "SLOT-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/imaging/appointments/SLOT-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("lists appointment slots for patient", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ id: "SLOT-1" }] });
    const res = await listAppointmentSlotsForPatient("PAT-1");
    expect(res).toEqual([{ id: "SLOT-1" }]);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/imaging/appointments?patientId=PAT-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("updates appointment slot status via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { id: "SLOT-1", status: "CONFIRMED" } });
    const res = await updateAppointmentSlotStatus("SLOT-1", "CONFIRMED");
    expect(res).toEqual({ id: "SLOT-1", status: "CONFIRMED" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/imaging/appointments/SLOT-1/status",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  // BCM-IMG-002: Reception
  it("registers reception check-in via POST", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { id: "INTAKE-1" } });
    const res = await checkInReception({
      appointmentSlotId: "SLOT-1",
      patientId: "PAT-1",
      preparationVerified: true,
      intakeNotes: "Verified",
    });
    expect(res).toEqual({ id: "INTAKE-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/imaging/receptions",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("gets reception intake by ID and slot", async () => {
    mockFetchOnce({ jsonBody: { id: "INTAKE-1" } });
    expect(await getReceptionIntake("INTAKE-1")).toEqual({ id: "INTAKE-1" });

    mockFetchOnce({ jsonBody: { id: "INTAKE-1" } });
    expect(await getReceptionIntakeBySlot("SLOT-1")).toEqual({ id: "INTAKE-1" });
  });

  // BCM-IMG-003: Study Management
  it("creates, gets, lists and updates studies", async () => {
    mockFetchOnce({ jsonBody: { id: "STUDY-1" } });
    expect(
      await createStudy({ accessionNumber: "ACC-1", patientId: "PAT-1", modality: "CT" }),
    ).toEqual({ id: "STUDY-1" });

    mockFetchOnce({ jsonBody: { id: "STUDY-1" } });
    expect(await getStudy("STUDY-1")).toEqual({ id: "STUDY-1" });

    mockFetchOnce({ jsonBody: [{ id: "STUDY-1" }] });
    expect(await listStudiesForPatient("PAT-1")).toEqual([{ id: "STUDY-1" }]);

    mockFetchOnce({ jsonBody: { id: "STUDY-1", status: "COMPLETED" } });
    expect(
      await updateStudyStatus("STUDY-1", {
        seriesCount: 2,
        instanceCount: 100,
        status: "COMPLETED",
      }),
    ).toEqual({ id: "STUDY-1", status: "COMPLETED" });
  });

  // BCM-IMG-004: DICOM Integration
  it("handles DICOM configs and operations", async () => {
    mockFetchOnce({ jsonBody: { id: "CFG-1" } });
    expect(
      await registerDicomConfig({
        aeTitle: "PACS",
        host: "127.0.0.1",
        port: 104,
        modalityType: "CT",
      }),
    ).toEqual({ id: "CFG-1" });

    mockFetchOnce({ jsonBody: { id: "CFG-1" } });
    expect(await getDicomConfig("CFG-1")).toEqual({ id: "CFG-1" });

    mockFetchOnce({ jsonBody: [{ id: "CFG-1" }] });
    expect(await listDicomConfigs()).toEqual([{ id: "CFG-1" }]);

    mockFetchOnce({ jsonBody: { result: "Success" } });
    expect(await echoCEcho("CFG-1")).toEqual({ result: "Success" });

    mockFetchOnce({ jsonBody: [{ patientId: "PAT-1" }] });
    expect(await queryDicomWorklist("CFG-1", "PAT-1", "CT")).toEqual([{ patientId: "PAT-1" }]);

    mockFetchOnce({ jsonBody: { transferId: "XFER-1" } });
    expect(await requestDicomTransfer("CFG-1", "UID-1", "DEST")).toEqual({ transferId: "XFER-1" });

    mockFetchOnce({ jsonBody: { validHeader: true } });
    expect(await validateDicomHeader("CFG-1", "PAT-1", "UID-1", "CT")).toEqual({
      validHeader: true,
    });
  });

  // BCM-IMG-005: PACS Integration
  it("handles PACS endpoints and web operations", async () => {
    mockFetchOnce({ jsonBody: { id: "PACS-1" } });
    expect(
      await registerPacsEndpoint({
        pacsNodeId: "NODE-1",
        baseUrl: "https://pacs.org",
        protocol: "DICOM_WEB",
      }),
    ).toEqual({ id: "PACS-1" });

    mockFetchOnce({ jsonBody: { id: "PACS-1" } });
    expect(await getPacsEndpoint("PACS-1")).toEqual({ id: "PACS-1" });

    mockFetchOnce({ jsonBody: [{ id: "PACS-1" }] });
    expect(await listPacsEndpoints()).toEqual([{ id: "PACS-1" }]);

    mockFetchOnce({ jsonBody: { result: "Found" } });
    expect(await queryPacsStudy("PACS-1", "ACC-1")).toEqual({ result: "Found" });

    mockFetchOnce({ jsonBody: [{ studyInstanceUid: "UID-1" }] });
    expect(await qidoSearchPacsStudies("PACS-1", "PAT-1", "CT")).toEqual([
      { studyInstanceUid: "UID-1" },
    ]);

    mockFetchOnce({ jsonBody: { wadoUrl: "https://wado.org" } });
    expect(await getPacsWadoUrl("PACS-1", "UID-1")).toEqual({ wadoUrl: "https://wado.org" });

    mockFetchOnce({ jsonBody: { status: "STORED" } });
    expect(await stowStorePacs("PACS-1", "UID-1", "application/dicom", "base64")).toEqual({
      status: "STORED",
    });
  });

  // BCM-IMG-006: Dictation
  it("handles medical dictations", async () => {
    mockFetchOnce({ jsonBody: { id: "DICT-1" } });
    expect(await createDictation({ studyId: "STUDY-1", dictationText: "Text" })).toEqual({
      id: "DICT-1",
    });

    mockFetchOnce({ jsonBody: { id: "DICT-1" } });
    expect(await getDictation("DICT-1")).toEqual({ id: "DICT-1" });

    mockFetchOnce({ jsonBody: [{ id: "DICT-1" }] });
    expect(await listDictationsForStudy("STUDY-1")).toEqual([{ id: "DICT-1" }]);
  });

  // BCM-IMG-007: Radiology Signature
  it("handles radiology reports and signature", async () => {
    mockFetchOnce({ jsonBody: { id: "REP-1" } });
    expect(
      await createRadiologyReport({
        studyId: "STUDY-1",
        findingsText: "Fine",
        impressionText: "Normal",
      }),
    ).toEqual({ id: "REP-1" });

    mockFetchOnce({ jsonBody: { id: "REP-1", signed: true } });
    expect(await signRadiologyReport("REP-1")).toEqual({ id: "REP-1", signed: true });

    mockFetchOnce({ jsonBody: { id: "REP-1" } });
    expect(await getRadiologyReport("REP-1")).toEqual({ id: "REP-1" });

    mockFetchOnce({ jsonBody: [{ id: "REP-1" }] });
    expect(await listRadiologyReportsForStudy("STUDY-1")).toEqual([{ id: "REP-1" }]);
  });

  // BCM-IMG-008: Study Delivery
  it("handles study delivery packages", async () => {
    mockFetchOnce({ jsonBody: { id: "PKG-1" } });
    expect(
      await createDeliveryPackage({
        studyId: "STUDY-1",
        patientId: "PAT-1",
        deliveryFormat: "PDF",
      }),
    ).toEqual({ id: "PKG-1" });

    mockFetchOnce({ jsonBody: { id: "PKG-1" } });
    expect(await getDeliveryPackage("PKG-1")).toEqual({ id: "PKG-1" });

    mockFetchOnce({ jsonBody: [{ id: "PKG-1" }] });
    expect(await listDeliveryPackagesForPatient("PAT-1")).toEqual([{ id: "PKG-1" }]);

    mockFetchOnce({ jsonBody: { id: "PKG-1", status: "DELIVERED" } });
    expect(await markDeliveryPackageDelivered("PKG-1")).toEqual({
      id: "PKG-1",
      status: "DELIVERED",
    });
  });
});
