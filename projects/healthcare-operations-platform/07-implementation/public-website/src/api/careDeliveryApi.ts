import { post } from "./httpClient";
import type {
  PublicAppointmentIntakeResult,
  PublicAppointmentRequestBody,
  PublicQuotationIntakeResult,
  PublicQuotationRequestBody,
} from "./types";

const BASE = "/api/public/care-delivery";

export function submitAppointmentRequest(
  body: PublicAppointmentRequestBody,
): Promise<PublicAppointmentIntakeResult> {
  return post(`${BASE}/appointment-requests`, body);
}

export function submitQuotationRequest(
  body: PublicQuotationRequestBody,
): Promise<PublicQuotationIntakeResult> {
  return post(`${BASE}/quotation-requests`, body);
}
