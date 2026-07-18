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
