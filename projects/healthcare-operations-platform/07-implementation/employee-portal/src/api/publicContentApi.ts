import { get } from "./httpClient";
import type {
  PublicDiagnosticServiceSnapshot,
  PublicPanelSnapshot,
  PublicPreparationSnapshot,
  PublicTestSnapshot,
} from "./types";

const BASE = "/api/public/catalog";

// -- BCM-SVC-001/002/003/005: staff review of what the public website currently shows -----------

export function listPublishedDiagnosticServices(
  laboratoryId: string,
): Promise<PublicDiagnosticServiceSnapshot[]> {
  return get(
    `${BASE}/diagnostic-services/published?laboratoryId=${encodeURIComponent(laboratoryId)}`,
  );
}

export function listPublishedTests(laboratoryId: string): Promise<PublicTestSnapshot[]> {
  return get(`${BASE}/tests/published?laboratoryId=${encodeURIComponent(laboratoryId)}`);
}

export function listPublishedPanels(laboratoryId: string): Promise<PublicPanelSnapshot[]> {
  return get(`${BASE}/panels/published?laboratoryId=${encodeURIComponent(laboratoryId)}`);
}

export function listPublishedPreparations(
  laboratoryId: string,
): Promise<PublicPreparationSnapshot[]> {
  return get(`${BASE}/preparations/published?laboratoryId=${encodeURIComponent(laboratoryId)}`);
}
