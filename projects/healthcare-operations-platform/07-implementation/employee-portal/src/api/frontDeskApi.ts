import { get, post } from "./httpClient";
import type {
  AcceptOrderRequest,
  AdmissionCatalogSelection,
  AdmissionRequest,
  AppointmentSlot,
  CancelAppointmentRequest,
  CancelOrderRequest,
  CancelQuotationRequest,
  CommitAdmissionRequestRequest,
  ConfirmReceptionIdentityRequest,
  CreateDiagnosticOrderRequest,
  DiagnosticOrder,
  IssueQuotationRequest,
  MarkAdmissionReadyRequest,
  OrderLine,
  PreparationInstruction,
  PriceOrderRequest,
  QuotationLine,
  QuotationRequest,
  ReceptionVisit,
  RejectAdmissionRequestRequest,
  RequestAppointmentRequest,
  RequestedCatalogItem,
  StartAdmissionRequestRequest,
  StartQuotationRequest,
  StartReceptionVisitRequest,
  UpdateReceptionPriorityRequest,
} from "./types";

const RECEPTION_VISITS_BASE = "/api/care-delivery/reception-visits";
const DIAGNOSTIC_ORDERS_BASE = "/api/clinical-operations/diagnostic-orders";
const APPOINTMENTS_BASE = "/api/care-delivery/appointments";
const ADMISSION_REQUESTS_BASE = "/api/care-delivery/admission-requests";
const QUOTATIONS_BASE = "/api/care-delivery/quotations";

// -- BCM-ATT-003 Reception Management --------------------------------------------------------------

export function listReceptionVisits(tenantId: string): Promise<ReceptionVisit[]> {
  const query = new URLSearchParams({ tenantId });
  return get<ReceptionVisit[]>(`${RECEPTION_VISITS_BASE}?${query.toString()}`);
}

export function getReceptionVisit(visitId: string): Promise<ReceptionVisit> {
  return get<ReceptionVisit>(`${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}`);
}

export function startReceptionVisit(request: StartReceptionVisitRequest): Promise<ReceptionVisit> {
  return post<ReceptionVisit, StartReceptionVisitRequest>(RECEPTION_VISITS_BASE, request);
}

export function confirmReceptionIdentity(
  visitId: string,
  request: ConfirmReceptionIdentityRequest,
): Promise<ReceptionVisit> {
  return post<ReceptionVisit, ConfirmReceptionIdentityRequest>(
    `${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}/confirm-identity`,
    request,
  );
}

export function advanceReceptionToAdmission(visitId: string): Promise<ReceptionVisit> {
  return post<ReceptionVisit, Record<string, never>>(
    `${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}/advance-to-admission`,
    {},
  );
}

export function updateReceptionPriority(
  visitId: string,
  request: UpdateReceptionPriorityRequest,
): Promise<ReceptionVisit> {
  return post<ReceptionVisit, UpdateReceptionPriorityRequest>(
    `${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}/priority`,
    request,
  );
}

export function abandonReceptionVisit(visitId: string): Promise<ReceptionVisit> {
  return post<ReceptionVisit, Record<string, never>>(
    `${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}/abandon`,
    {},
  );
}

// -- BCM-LAB-001 Diagnostic Order Management --------------------------------------------------------

export function listDiagnosticOrders(tenantId: string): Promise<DiagnosticOrder[]> {
  const query = new URLSearchParams({ tenantId });
  return get<DiagnosticOrder[]>(`${DIAGNOSTIC_ORDERS_BASE}?${query.toString()}`);
}

export function getDiagnosticOrder(orderId: string): Promise<DiagnosticOrder> {
  return get<DiagnosticOrder>(`${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}`);
}

export function listDiagnosticOrderLines(orderId: string): Promise<OrderLine[]> {
  return get<OrderLine[]>(`${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/lines`);
}

export function createDiagnosticOrder(
  request: CreateDiagnosticOrderRequest,
): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, CreateDiagnosticOrderRequest>(DIAGNOSTIC_ORDERS_BASE, request);
}

export function priceDiagnosticOrder(
  orderId: string,
  request?: PriceOrderRequest,
): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, PriceOrderRequest>(
    `${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/price`,
    request ?? {},
  );
}

export function acceptDiagnosticOrder(
  orderId: string,
  request?: AcceptOrderRequest,
): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, AcceptOrderRequest>(
    `${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/accept`,
    request ?? {},
  );
}

export function cancelDiagnosticOrder(
  orderId: string,
  request: CancelOrderRequest,
): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, CancelOrderRequest>(
    `${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/cancel`,
    request,
  );
}

export function completeDiagnosticOrder(orderId: string): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, Record<string, never>>(
    `${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/complete`,
    {},
  );
}

// -- BCM-ATT-001 Appointment Scheduling ------------------------------------------------------------

export function listAppointments(tenantId: string): Promise<AppointmentSlot[]> {
  const query = new URLSearchParams({ tenantId });
  return get<AppointmentSlot[]>(`${APPOINTMENTS_BASE}?${query.toString()}`);
}

export function getAppointment(appointmentId: string): Promise<AppointmentSlot> {
  return get<AppointmentSlot>(`${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}`);
}

export function listAppointmentRequestedItems(
  appointmentId: string,
): Promise<RequestedCatalogItem[]> {
  return get<RequestedCatalogItem[]>(
    `${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}/requested-items`,
  );
}

export function getAppointmentPreparationInstructions(
  appointmentId: string,
): Promise<PreparationInstruction[]> {
  return get<PreparationInstruction[]>(
    `${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}/preparation-instructions`,
  );
}

export function requestAppointment(request: RequestAppointmentRequest): Promise<AppointmentSlot> {
  return post<AppointmentSlot, RequestAppointmentRequest>(APPOINTMENTS_BASE, request);
}

export function confirmAppointment(appointmentId: string): Promise<AppointmentSlot> {
  return post<AppointmentSlot, Record<string, never>>(
    `${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}/confirm`,
    {},
  );
}

export function checkInAppointment(appointmentId: string): Promise<AppointmentSlot> {
  return post<AppointmentSlot, Record<string, never>>(
    `${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}/check-in`,
    {},
  );
}

export function cancelAppointment(
  appointmentId: string,
  request?: CancelAppointmentRequest,
): Promise<AppointmentSlot> {
  return post<AppointmentSlot, CancelAppointmentRequest>(
    `${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}/cancel`,
    request ?? {},
  );
}

export function markAppointmentNoShow(appointmentId: string): Promise<AppointmentSlot> {
  return post<AppointmentSlot, Record<string, never>>(
    `${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}/no-show`,
    {},
  );
}

// -- BCM-ATT-004 Admission Management ---------------------------------------------------------------

export function listAdmissionRequests(tenantId: string): Promise<AdmissionRequest[]> {
  const query = new URLSearchParams({ tenantId });
  return get<AdmissionRequest[]>(`${ADMISSION_REQUESTS_BASE}?${query.toString()}`);
}

export function getAdmissionRequest(admissionId: string): Promise<AdmissionRequest> {
  return get<AdmissionRequest>(`${ADMISSION_REQUESTS_BASE}/${encodeURIComponent(admissionId)}`);
}

export function listAdmissionCatalogSelections(
  admissionId: string,
): Promise<AdmissionCatalogSelection[]> {
  return get<AdmissionCatalogSelection[]>(
    `${ADMISSION_REQUESTS_BASE}/${encodeURIComponent(admissionId)}/catalog-selections`,
  );
}

export function startAdmissionRequest(
  request: StartAdmissionRequestRequest,
): Promise<AdmissionRequest> {
  return post<AdmissionRequest, StartAdmissionRequestRequest>(ADMISSION_REQUESTS_BASE, request);
}

export function markAdmissionReady(
  admissionId: string,
  request: MarkAdmissionReadyRequest,
): Promise<AdmissionRequest> {
  return post<AdmissionRequest, MarkAdmissionReadyRequest>(
    `${ADMISSION_REQUESTS_BASE}/${encodeURIComponent(admissionId)}/mark-ready`,
    request,
  );
}

export function commitAdmissionRequest(
  admissionId: string,
  request: CommitAdmissionRequestRequest,
): Promise<AdmissionRequest> {
  return post<AdmissionRequest, CommitAdmissionRequestRequest>(
    `${ADMISSION_REQUESTS_BASE}/${encodeURIComponent(admissionId)}/commit`,
    request,
  );
}

export function rejectAdmissionRequest(
  admissionId: string,
  request?: RejectAdmissionRequestRequest,
): Promise<AdmissionRequest> {
  return post<AdmissionRequest, RejectAdmissionRequestRequest>(
    `${ADMISSION_REQUESTS_BASE}/${encodeURIComponent(admissionId)}/reject`,
    request ?? {},
  );
}

// -- BCM-ATT-006 Quotation Management ----------------------------------------------------------------

export function listQuotations(tenantId: string): Promise<QuotationRequest[]> {
  const query = new URLSearchParams({ tenantId });
  return get<QuotationRequest[]>(`${QUOTATIONS_BASE}?${query.toString()}`);
}

export function getQuotation(quotationId: string): Promise<QuotationRequest> {
  return get<QuotationRequest>(`${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}`);
}

export function listQuotationLines(quotationId: string): Promise<QuotationLine[]> {
  return get<QuotationLine[]>(`${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}/lines`);
}

export function startQuotation(request: StartQuotationRequest): Promise<QuotationRequest> {
  return post<QuotationRequest, StartQuotationRequest>(QUOTATIONS_BASE, request);
}

export function issueQuotation(
  quotationId: string,
  request?: IssueQuotationRequest,
): Promise<QuotationRequest> {
  return post<QuotationRequest, IssueQuotationRequest>(
    `${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}/issue`,
    request ?? {},
  );
}

export function acceptQuotation(quotationId: string): Promise<QuotationRequest> {
  return post<QuotationRequest, Record<string, never>>(
    `${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}/accept`,
    {},
  );
}

export function convertQuotation(quotationId: string): Promise<QuotationRequest> {
  return post<QuotationRequest, Record<string, never>>(
    `${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}/convert`,
    {},
  );
}

export function cancelQuotation(
  quotationId: string,
  request?: CancelQuotationRequest,
): Promise<QuotationRequest> {
  return post<QuotationRequest, CancelQuotationRequest>(
    `${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}/cancel`,
    request ?? {},
  );
}

export function expireQuotation(quotationId: string): Promise<QuotationRequest> {
  return post<QuotationRequest, Record<string, never>>(
    `${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}/expire`,
    {},
  );
}
