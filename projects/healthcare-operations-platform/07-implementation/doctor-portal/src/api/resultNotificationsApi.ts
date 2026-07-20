import { get } from "./httpClient";
import type { ResultNotificationRequest } from "./types";

/**
 * Lists notification records for a released result. Doctor-portal callers only pass a
 * `resultId` that was itself obtained from an already-authorized
 * {@link ../api/patientResultHistoryApi.getPatientHistoryAsDoctor} call for a referred patient,
 * so the transitive authorization matches the pattern already accepted for the patient portal
 * (COM-MOD-009-PORTAL-001); the backend still requires PORTAL_DOCTOR_NOTIFICATIONS_VIEW.
 */
export function getResultNotifications(
  resultId: string,
  tenantId: string,
): Promise<ResultNotificationRequest[]> {
  const params = new URLSearchParams({ tenantId });
  return get(
    `/api/clinical-operations/laboratory-results/${resultId}/notifications?${params.toString()}`,
  );
}
