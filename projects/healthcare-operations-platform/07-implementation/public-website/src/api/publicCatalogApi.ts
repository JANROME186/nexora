import { get } from "./httpClient";
import type {
  PublicDiagnosticServiceSnapshot,
  PublicPanelSnapshot,
  PublicPreparationSnapshot,
  PublicTestSnapshot,
} from "./types";

const BASE = "/api/public/catalog";

export function listDiagnosticServices(
  laboratoryId: string,
): Promise<PublicDiagnosticServiceSnapshot[]> {
  return get(
    `${BASE}/diagnostic-services/published?laboratoryId=${encodeURIComponent(laboratoryId)}`,
  );
}

export function getDiagnosticServiceSnapshot(
  serviceId: string,
): Promise<PublicDiagnosticServiceSnapshot> {
  return get(`${BASE}/diagnostic-services/${encodeURIComponent(serviceId)}/published-snapshot`);
}

export function listTests(laboratoryId: string): Promise<PublicTestSnapshot[]> {
  return get(`${BASE}/tests/published?laboratoryId=${encodeURIComponent(laboratoryId)}`);
}

export function getTestSnapshot(testId: string): Promise<PublicTestSnapshot> {
  return get(`${BASE}/tests/${encodeURIComponent(testId)}/published-snapshot`);
}

export function listPanels(laboratoryId: string): Promise<PublicPanelSnapshot[]> {
  return get(`${BASE}/panels/published?laboratoryId=${encodeURIComponent(laboratoryId)}`);
}

export function getPanelSnapshot(panelId: string): Promise<PublicPanelSnapshot> {
  return get(`${BASE}/panels/${encodeURIComponent(panelId)}/published-snapshot`);
}

export function listPreparations(laboratoryId: string): Promise<PublicPreparationSnapshot[]> {
  return get(`${BASE}/preparations/published?laboratoryId=${encodeURIComponent(laboratoryId)}`);
}

export function getPreparationSnapshot(preparationId: string): Promise<PublicPreparationSnapshot> {
  return get(`${BASE}/preparations/${encodeURIComponent(preparationId)}/published-snapshot`);
}
