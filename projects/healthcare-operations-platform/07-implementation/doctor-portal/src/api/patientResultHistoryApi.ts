import { get } from "./httpClient";

export interface ResultHistoryEntry {
  resultId: string;
  analyteName: string;
  stringValue: string;
  referenceRange: string;
  isAbnormal: boolean;
  releasedAt: string;
}

export interface PatientResultHistoryView {
  patientId: string;
  entries: ResultHistoryEntry[];
}

export async function getPatientHistory(patientId: string): Promise<PatientResultHistoryView> {
  return get(`/api/results/history/patient/${patientId}`);
}

/**
 * COM-MOD-009-PORTAL-002: requests a referred patient's result history as a referring doctor.
 * The backend (`ResultHistoryController`/`ResultHistoryService`) verifies `doctorId` has actually
 * referred `patientId` (via `ReferringDoctorAuthorizationPort`) before returning any data; an
 * unreferred patient results in a 403 that surfaces as an `ApiError` with `status === 403`.
 */
export async function getPatientHistoryAsDoctor(
  patientId: string,
  tenantId: string,
  doctorId: string,
): Promise<PatientResultHistoryView> {
  const params = new URLSearchParams({
    tenantId,
    callerRoleCode: "REFERRING_DOCTOR",
    callerId: doctorId,
  });
  return get(`/api/results/history/patient/${patientId}?${params.toString()}`);
}
