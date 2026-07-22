import { get, post } from "./httpClient";
import type {
  AppointmentSlot,
  CancelAppointmentRequest,
  CancelQuotationRequest,
  IssueQuotationRequest,
  QuotationRequest,
} from "./types";

const APPOINTMENTS_BASE = "/api/care-delivery/appointments";
const QUOTATIONS_BASE = "/api/care-delivery/quotations";

// -- BCM-ATT-001: staff triage of public-website appointment requests ---------------------------

export function listAppointments(tenantId: string): Promise<AppointmentSlot[]> {
  const query = new URLSearchParams({ tenantId });
  return get<AppointmentSlot[]>(`${APPOINTMENTS_BASE}?${query.toString()}`);
}

export function confirmAppointment(appointmentId: string): Promise<AppointmentSlot> {
  return post<AppointmentSlot, Record<string, never>>(
    `${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}/confirm`,
    {},
  );
}

export function cancelAppointment(
  appointmentId: string,
  request: CancelAppointmentRequest,
): Promise<AppointmentSlot> {
  return post<AppointmentSlot, CancelAppointmentRequest>(
    `${APPOINTMENTS_BASE}/${encodeURIComponent(appointmentId)}/cancel`,
    request,
  );
}

// -- BCM-ATT-006: staff triage of public-website quotation requests -----------------------------

export function listQuotations(tenantId: string): Promise<QuotationRequest[]> {
  const query = new URLSearchParams({ tenantId });
  return get<QuotationRequest[]>(`${QUOTATIONS_BASE}?${query.toString()}`);
}

export function issueQuotation(
  quotationId: string,
  request: IssueQuotationRequest,
): Promise<QuotationRequest> {
  return post<QuotationRequest, IssueQuotationRequest>(
    `${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}/issue`,
    request,
  );
}

export function cancelQuotation(
  quotationId: string,
  request: CancelQuotationRequest,
): Promise<QuotationRequest> {
  return post<QuotationRequest, CancelQuotationRequest>(
    `${QUOTATIONS_BASE}/${encodeURIComponent(quotationId)}/cancel`,
    request,
  );
}
