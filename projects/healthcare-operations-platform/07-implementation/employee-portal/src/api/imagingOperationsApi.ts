/**
 * Imaging Operations API client (COM-MOD-014-FE-001).
 *
 * Covers employee-portal administration endpoints generated from BCM-IMG-001 through BCM-IMG-008:
 *   ImagingAppointmentSchedulingController /api/v1/imaging/appointments
 *   ImagingReceptionController             /api/v1/imaging/receptions
 *   ImagingStudyManagementController       /api/v1/imaging/studies
 *   DicomIntegrationController             /api/v1/imaging/dicom-configs
 *   PacsIntegrationController              /api/v1/imaging/pacs-endpoints
 *   MedicalDictationController             /api/v1/imaging/dictations
 *   RadiologySignatureController           /api/v1/imaging/reports
 *   ImagingStudyDeliveryController         /api/v1/imaging/delivery-packages
 */
import { get, post, put } from "./httpClient";

export interface ImagingAppointmentSlot {
  id: string;
  tenantId: string;
  patientId: string;
  branchId: string;
  modality: string;
  procedureCode: string;
  procedureRoomId: string;
  startTime: string;
  durationMinutes: number;
  notes?: string;
  status: string;
  createdBy?: string;
  createdAt?: string;
}

export interface ScheduleSlotRequest {
  patientId: string;
  branchId: string;
  modality: string;
  procedureCode: string;
  procedureRoomId: string;
  startTime: string;
  durationMinutes: number;
  notes?: string;
}

export interface ImagingReceptionIntake {
  id: string;
  tenantId: string;
  appointmentSlotId: string;
  patientId: string;
  preparationVerified: boolean;
  intakeNotes?: string;
  status: string;
  receivedBy?: string;
  receivedAt?: string;
}

export interface CheckInRequest {
  appointmentSlotId: string;
  patientId: string;
  preparationVerified: boolean;
  intakeNotes?: string;
}

export interface ImagingStudy {
  id: string;
  tenantId: string;
  accessionNumber: string;
  patientId: string;
  modality: string;
  studyDescription?: string;
  seriesCount: number;
  instanceCount: number;
  status: string;
  createdBy?: string;
  createdAt?: string;
}

export interface CreateStudyRequest {
  accessionNumber: string;
  patientId: string;
  modality: string;
  studyDescription?: string;
}

export interface UpdateStudyStatusRequest {
  seriesCount: number;
  instanceCount: number;
  status: string;
}

export interface DicomAdapterConfiguration {
  id: string;
  tenantId: string;
  aeTitle: string;
  host: string;
  port: number;
  modalityType: string;
  status: string;
  createdBy?: string;
  createdAt?: string;
}

export interface RegisterDicomConfigRequest {
  aeTitle: string;
  host: string;
  port: number;
  modalityType: string;
}

export interface DicomWorklistEntry {
  patientId: string;
  patientName: string;
  accessionNumber: string;
  modality: string;
  scheduledProcedureStepId: string;
  scheduledDate: string;
}

export interface DicomTransferResult {
  transferId: string;
  studyInstanceUid: string;
  destinationAeTitle: string;
  status: string;
  transferredInstances: number;
  message: string;
}

export interface DicomValidationResult {
  patientId: string;
  studyInstanceUid: string;
  modality: string;
  validHeader: boolean;
  validationMessage: string;
}

export interface PacsIntegrationEndpoint {
  id: string;
  tenantId: string;
  pacsNodeId: string;
  baseUrl: string;
  protocol: string;
  status: string;
  createdBy?: string;
  createdAt?: string;
}

export interface RegisterPacsEndpointRequest {
  pacsNodeId: string;
  baseUrl: string;
  protocol: string;
}

export interface PacsQidoSearchResult {
  studyInstanceUid: string;
  patientId: string;
  patientName: string;
  studyDate: string;
  modality: string;
  numberOfStudyRelatedInstances: number;
}

export interface PacsWadoRetrieveResponse {
  studyInstanceUid: string;
  wadoUrl: string;
  contentType: string;
}

export interface PacsStowStoreResult {
  studyInstanceUid: string;
  status: string;
  storedInstances: number;
  responseMessage: string;
}

export interface RadiologyDictation {
  id: string;
  tenantId: string;
  studyId: string;
  dictationText: string;
  audioReferenceUrl?: string;
  status: string;
  dictatedBy?: string;
  dictatedAt?: string;
}

export interface CreateDictationRequest {
  studyId: string;
  dictationText: string;
  audioReferenceUrl?: string;
}

export interface RadiologyReport {
  id: string;
  tenantId: string;
  studyId: string;
  findingsText: string;
  impressionText: string;
  signed: boolean;
  signedBy?: string;
  signedAt?: string;
  createdBy?: string;
  createdAt?: string;
}

export interface CreateReportRequest {
  studyId: string;
  findingsText: string;
  impressionText: string;
}

export interface ImagingDeliveryPackage {
  id: string;
  tenantId: string;
  studyId: string;
  patientId: string;
  deliveryFormat: string;
  status: string;
  deliveredBy?: string;
  deliveredAt?: string;
  createdBy?: string;
  createdAt?: string;
}

export interface CreateDeliveryPackageRequest {
  studyId: string;
  patientId: string;
  deliveryFormat: string;
}

// BCM-IMG-001: Appointment Scheduling
export async function scheduleAppointmentSlot(request: ScheduleSlotRequest): Promise<ImagingAppointmentSlot> {
  return post<ImagingAppointmentSlot>("/api/v1/imaging/appointments", request);
}

export async function getAppointmentSlot(slotId: string): Promise<ImagingAppointmentSlot> {
  return get<ImagingAppointmentSlot>(`/api/v1/imaging/appointments/${slotId}`);
}

export async function listAppointmentSlotsForPatient(patientId: string): Promise<ImagingAppointmentSlot[]> {
  return get<ImagingAppointmentSlot[]>(`/api/v1/imaging/appointments?patientId=${encodeURIComponent(patientId)}`);
}

export async function updateAppointmentSlotStatus(slotId: string, status: string): Promise<ImagingAppointmentSlot> {
  return put<ImagingAppointmentSlot>(`/api/v1/imaging/appointments/${slotId}/status`, { status });
}

// BCM-IMG-002: Reception Intake
export async function checkInReception(request: CheckInRequest): Promise<ImagingReceptionIntake> {
  return post<ImagingReceptionIntake>("/api/v1/imaging/receptions", request);
}

export async function getReceptionIntake(intakeId: string): Promise<ImagingReceptionIntake> {
  return get<ImagingReceptionIntake>(`/api/v1/imaging/receptions/${intakeId}`);
}

export async function getReceptionIntakeBySlot(appointmentSlotId: string): Promise<ImagingReceptionIntake> {
  return get<ImagingReceptionIntake>(`/api/v1/imaging/receptions?appointmentSlotId=${encodeURIComponent(appointmentSlotId)}`);
}

// BCM-IMG-003: Study Management
export async function createStudy(request: CreateStudyRequest): Promise<ImagingStudy> {
  return post<ImagingStudy>("/api/v1/imaging/studies", request);
}

export async function getStudy(studyId: string): Promise<ImagingStudy> {
  return get<ImagingStudy>(`/api/v1/imaging/studies/${studyId}`);
}

export async function listStudiesForPatient(patientId: string): Promise<ImagingStudy[]> {
  return get<ImagingStudy[]>(`/api/v1/imaging/studies?patientId=${encodeURIComponent(patientId)}`);
}

export async function updateStudyStatus(studyId: string, request: UpdateStudyStatusRequest): Promise<ImagingStudy> {
  return put<ImagingStudy>(`/api/v1/imaging/studies/${studyId}/status`, request);
}

// BCM-IMG-004: DICOM Integration
export async function registerDicomConfig(request: RegisterDicomConfigRequest): Promise<DicomAdapterConfiguration> {
  return post<DicomAdapterConfiguration>("/api/v1/imaging/dicom-configs", request);
}

export async function getDicomConfig(configurationId: string): Promise<DicomAdapterConfiguration> {
  return get<DicomAdapterConfiguration>(`/api/v1/imaging/dicom-configs/${configurationId}`);
}

export async function listDicomConfigs(): Promise<DicomAdapterConfiguration[]> {
  return get<DicomAdapterConfiguration[]>("/api/v1/imaging/dicom-configs");
}

export async function echoCEcho(configurationId: string): Promise<{ result: string }> {
  return post<{ result: string }>(`/api/v1/imaging/dicom-configs/${configurationId}/echo`, {});
}

export async function queryDicomWorklist(configurationId: string, patientId?: string, modality?: string): Promise<DicomWorklistEntry[]> {
  const query = new URLSearchParams();
  if (patientId) query.set("patientId", patientId);
  if (modality) query.set("modality", modality);
  const qStr = query.toString() ? `?${query.toString()}` : "";
  return get<DicomWorklistEntry[]>(`/api/v1/imaging/dicom-configs/${configurationId}/worklist${qStr}`);
}

export async function requestDicomTransfer(configurationId: string, studyInstanceUid: string, destinationAeTitle: string): Promise<DicomTransferResult> {
  return post<DicomTransferResult>(`/api/v1/imaging/dicom-configs/${configurationId}/transfer`, { studyInstanceUid, destinationAeTitle });
}

export async function validateDicomHeader(configurationId: string, patientId: string, studyInstanceUid: string, modality: string): Promise<DicomValidationResult> {
  return post<DicomValidationResult>(`/api/v1/imaging/dicom-configs/${configurationId}/validate-header`, { patientId, studyInstanceUid, modality });
}

// BCM-IMG-005: PACS Integration
export async function registerPacsEndpoint(request: RegisterPacsEndpointRequest): Promise<PacsIntegrationEndpoint> {
  return post<PacsIntegrationEndpoint>("/api/v1/imaging/pacs-endpoints", request);
}

export async function getPacsEndpoint(endpointId: string): Promise<PacsIntegrationEndpoint> {
  return get<PacsIntegrationEndpoint>(`/api/v1/imaging/pacs-endpoints/${endpointId}`);
}

export async function listPacsEndpoints(): Promise<PacsIntegrationEndpoint[]> {
  return get<PacsIntegrationEndpoint[]>("/api/v1/imaging/pacs-endpoints");
}

export async function queryPacsStudy(endpointId: string, accessionNumber: string): Promise<{ result: string }> {
  return get<{ result: string }>(`/api/v1/imaging/pacs-endpoints/${endpointId}/query?accessionNumber=${encodeURIComponent(accessionNumber)}`);
}

export async function qidoSearchPacsStudies(endpointId: string, patientId?: string, modality?: string): Promise<PacsQidoSearchResult[]> {
  const query = new URLSearchParams();
  if (patientId) query.set("patientId", patientId);
  if (modality) query.set("modality", modality);
  const qStr = query.toString() ? `?${query.toString()}` : "";
  return get<PacsQidoSearchResult[]>(`/api/v1/imaging/pacs-endpoints/${endpointId}/qido-search${qStr}`);
}

export async function getPacsWadoUrl(endpointId: string, studyInstanceUid: string, seriesInstanceUid?: string, objectUid?: string): Promise<PacsWadoRetrieveResponse> {
  const query = new URLSearchParams({ studyInstanceUid });
  if (seriesInstanceUid) query.set("seriesInstanceUid", seriesInstanceUid);
  if (objectUid) query.set("objectUid", objectUid);
  return get<PacsWadoRetrieveResponse>(`/api/v1/imaging/pacs-endpoints/${endpointId}/wado-url?${query.toString()}`);
}

export async function stowStorePacs(endpointId: string, studyInstanceUid: string, contentType: string, payloadBase64: string): Promise<PacsStowStoreResult> {
  return post<PacsStowStoreResult>(`/api/v1/imaging/pacs-endpoints/${endpointId}/stow-store`, { studyInstanceUid, contentType, payloadBase64 });
}

// BCM-IMG-006: Medical Dictation
export async function createDictation(request: CreateDictationRequest): Promise<RadiologyDictation> {
  return post<RadiologyDictation>("/api/v1/imaging/dictations", request);
}

export async function getDictation(dictationId: string): Promise<RadiologyDictation> {
  return get<RadiologyDictation>(`/api/v1/imaging/dictations/${dictationId}`);
}

export async function listDictationsForStudy(studyId: string): Promise<RadiologyDictation[]> {
  return get<RadiologyDictation[]>(`/api/v1/imaging/dictations?studyId=${encodeURIComponent(studyId)}`);
}

// BCM-IMG-007: Radiology Signature
export async function createRadiologyReport(request: CreateReportRequest): Promise<RadiologyReport> {
  return post<RadiologyReport>("/api/v1/imaging/reports", request);
}

export async function signRadiologyReport(reportId: string): Promise<RadiologyReport> {
  return post<RadiologyReport>(`/api/v1/imaging/reports/${reportId}/sign`, {});
}

export async function getRadiologyReport(reportId: string): Promise<RadiologyReport> {
  return get<RadiologyReport>(`/api/v1/imaging/reports/${reportId}`);
}

export async function listRadiologyReportsForStudy(studyId: string): Promise<RadiologyReport[]> {
  return get<RadiologyReport[]>(`/api/v1/imaging/reports?studyId=${encodeURIComponent(studyId)}`);
}

// BCM-IMG-008: Study Delivery
export async function createDeliveryPackage(request: CreateDeliveryPackageRequest): Promise<ImagingDeliveryPackage> {
  return post<ImagingDeliveryPackage>("/api/v1/imaging/delivery-packages", request);
}

export async function getDeliveryPackage(packageId: string): Promise<ImagingDeliveryPackage> {
  return get<ImagingDeliveryPackage>(`/api/v1/imaging/delivery-packages/${packageId}`);
}

export async function listDeliveryPackagesForPatient(patientId: string): Promise<ImagingDeliveryPackage[]> {
  return get<ImagingDeliveryPackage[]>(`/api/v1/imaging/delivery-packages?patientId=${encodeURIComponent(patientId)}`);
}

export async function markDeliveryPackageDelivered(packageId: string): Promise<ImagingDeliveryPackage> {
  return put<ImagingDeliveryPackage>(`/api/v1/imaging/delivery-packages/${packageId}/deliver`, {});
}
