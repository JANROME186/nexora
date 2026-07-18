/**
 * Results and Digital Delivery API client (MVP-MOD-007-FE-001).
 *
 * Covers employee-portal endpoints for:
 *   BCM-RES-001  Result Management     — result search / detail (read-only facade over LaboratoryResult)
 *   BCM-RES-002  PDF Report Generation — report history per result + regenerate action
 *   BCM-RES-006  Critical Results      — open escalation worklist + acknowledge / escalate / close
 *   BCM-RES-007  Result Notifications  — notification history per result
 *
 * BCM-RES-004 (Digital Delivery) and BCM-RES-005 (Result History) target the patient_portal and
 * doctor_portal surfaces only (ui-model.yaml surfaces.employee_portal.status = not_required for
 * both packages). Their backend endpoints are accessible to employee actors for operational
 * oversight via ResultDeliveryController but are not exposed as a dedicated employee-portal screen
 * in this backlog item. The authorize-delivery action is included here for the result-delivery
 * management surface used by internal staff.
 *
 * `LaboratoryResult` is owned by BCM-LAB-006 (MVP-MOD-006) and its wire shape (singular
 * analyteSnapshot/referenceRangeSnapshot/resultValue, no version field) predates this backlog and
 * is shared with already-closed screens (ResultReleaseScreen, TechnicalValidationScreen,
 * MedicalValidationScreen). Reshaping that shared contract is out of scope here (TD-FE-004); this
 * module instead normalizes the raw response into the existing `LaboratoryResult` FE type so the
 * new result-search screen renders correctly against the real backend.
 */
import { get, post } from "./httpClient";
import type {
  LaboratoryResult,
  GeneratedResultReport,
  ResultDeliveryTicket,
  CriticalResultEscalation,
  ResultNotificationRequest,
} from "./types";

const CLINICAL_BASE = "/api/clinical-operations";
const DELIVERY_BASE = "/api/results/delivery";
const ESCALATION_BASE = "/api/results/critical-escalations";

// ---- BCM-RES-001: Result Management (read-only) -----------------------------------------

import { toLaboratoryResult, type BackendLaboratoryResult } from "./laboratoryResultMapper";

/** List released results for a tenant (result search / worklist). */
export async function listReleasedResults(tenantId: string): Promise<LaboratoryResult[]> {
  const raw = await get<BackendLaboratoryResult[]>(
    `${CLINICAL_BASE}/laboratory-results?tenantId=${tenantId}&status=released`,
  );
  return raw.map(toLaboratoryResult);
}

/** Fetch a single laboratory result by ID. */
export async function getResultById(resultId: string, tenantId: string): Promise<LaboratoryResult> {
  const raw = await get<BackendLaboratoryResult>(
    `${CLINICAL_BASE}/laboratory-results/${resultId}?tenantId=${tenantId}`,
  );
  return toLaboratoryResult(raw);
}

// ---- BCM-RES-002: PDF Report Generation (employee-portal) --------------------------------

/** List generated reports for a result. */
export async function listResultReports(
  resultId: string,
  tenantId: string,
): Promise<GeneratedResultReport[]> {
  return get(`${CLINICAL_BASE}/laboratory-results/${resultId}/reports?tenantId=${tenantId}`);
}

/** Trigger report regeneration for a released result. */
export async function regenerateReport(
  resultId: string,
  tenantId: string,
  actorId: string,
): Promise<GeneratedResultReport> {
  return post(
    `${CLINICAL_BASE}/laboratory-results/${resultId}/reports/regenerate?tenantId=${tenantId}&actorId=${actorId}`,
    {},
  );
}

// ---- BCM-RES-004: Digital Delivery (employee-portal oversight only) ----------------------

/** Authorize delivery tickets for a released result (internal staff action). */
export async function authorizeDelivery(
  resultId: string,
  tenantId: string,
  actorId: string,
): Promise<ResultDeliveryTicket[]> {
  return post(
    `${DELIVERY_BASE}/authorize?resultId=${resultId}&tenantId=${tenantId}&actorId=${actorId}`,
    {},
  );
}

/** Retrieve a delivery ticket by ID (includes viewed/delivered status). */
export async function getDeliveryTicket(
  ticketId: string,
  tenantId: string,
  callerId: string,
  actorId: string,
): Promise<ResultDeliveryTicket> {
  return get(
    `${DELIVERY_BASE}/${ticketId}?tenantId=${tenantId}&callerId=${callerId}&actorId=${actorId}`,
  );
}

// ---- BCM-RES-006: Critical Results (employee-portal) ------------------------------------

/** List open/active critical result escalations for a tenant. */
export async function listOpenEscalations(tenantId: string): Promise<CriticalResultEscalation[]> {
  return get(`${ESCALATION_BASE}/open?tenantId=${tenantId}`);
}

/** Acknowledge a critical escalation. */
export async function acknowledgeCriticalEscalation(
  escalationId: string,
  userId: string,
  actorId: string,
): Promise<CriticalResultEscalation> {
  return post(
    `${ESCALATION_BASE}/${escalationId}/acknowledge?userId=${userId}&actorId=${actorId}`,
    {},
  );
}

/** Escalate a critical escalation to the next tier. */
export async function escalateCriticalEscalation(
  escalationId: string,
  actorId: string,
): Promise<CriticalResultEscalation> {
  return post(`${ESCALATION_BASE}/${escalationId}/escalate?actorId=${actorId}`, {});
}

/** Close a critical escalation (requires prior acknowledgement). */
export async function closeCriticalEscalation(
  escalationId: string,
  actorId: string,
): Promise<CriticalResultEscalation> {
  return post(`${ESCALATION_BASE}/${escalationId}/close?actorId=${actorId}`, {});
}

// ---- BCM-RES-007: Result Notifications (employee-portal) --------------------------------

/** List notification requests for a result (dispatch history). */
export async function listResultNotifications(
  resultId: string,
  tenantId: string,
): Promise<ResultNotificationRequest[]> {
  return get(`${CLINICAL_BASE}/laboratory-results/${resultId}/notifications?tenantId=${tenantId}`);
}
