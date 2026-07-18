import type { LaboratoryResult } from "./types";

/** Raw BCM-LAB-006 wire shape as actually returned by LaboratoryProcessingController. */
export interface BackendLaboratoryResult {
  resultId: string;
  tenantId: string;
  laboratoryId: string;
  sampleId: string;
  analyteSnapshot?: {
    testDefinitionId?: string;
    analyteId?: string;
    name?: string;
    unit?: string;
  } | null;
  referenceRangeSnapshot?: {
    referenceRangeId?: string;
    lowValue?: string | null;
    highValue?: string | null;
    criticalLowValue?: string | null;
    criticalHighValue?: string | null;
  } | null;
  resultValue?: {
    rawValue: string;
    numericValue?: number | null;
    unit?: string | null;
    method?: string | null;
    capturedAt: string;
    capturedBy?: string | null;
    deviceReference?: string | null;
  } | null;
  processingIncidents?: Array<{
    incidentType: string;
    notes?: string | null;
    recordedBy: string;
    recordedAt: string;
  }> | null;
  technicalValidation?: { validatedBy: string; validatedAt: string } | null;
  medicalValidation?: { validatedBy: string; validatedAt: string } | null;
  releaseRecord?: { releasedBy: string; releasedAt: string } | null;
  amendments?: Array<{
    amendedBy: string;
    amendedAt: string;
    amendmentReason: string;
  }> | null;
  status: string;
}

function toNumber(value: string | null | undefined): number | undefined {
  if (value === null || value === undefined) return undefined;
  const parsed = Number.parseFloat(value);
  return Number.isNaN(parsed) ? undefined : parsed;
}

function toAnalyteSnapshots(
  raw: BackendLaboratoryResult["analyteSnapshot"],
): LaboratoryResult["analyteSnapshots"] {
  if (!raw) return [];
  return [{ analyteRefId: raw.analyteId ?? "", name: raw.name ?? "", unit: raw.unit }];
}

function toReferenceRangeSnapshots(
  raw: BackendLaboratoryResult["referenceRangeSnapshot"],
): LaboratoryResult["referenceRangeSnapshots"] {
  if (!raw) return [];
  return [
    {
      rangeRefId: raw.referenceRangeId ?? "",
      normalLow: toNumber(raw.lowValue),
      normalHigh: toNumber(raw.highValue),
      criticalLow: toNumber(raw.criticalLowValue),
      criticalHigh: toNumber(raw.criticalHighValue),
    },
  ];
}

function toResultValues(
  raw: BackendLaboratoryResult["resultValue"],
): LaboratoryResult["resultValues"] {
  if (!raw) return [];
  return [
    {
      rawValue: raw.rawValue,
      numericValue: raw.numericValue ?? undefined,
      unit: raw.unit ?? undefined,
      method: raw.method ?? undefined,
      capturedAt: raw.capturedAt,
      capturedBy: raw.capturedBy ?? "",
      deviceReference: raw.deviceReference ?? undefined,
    },
  ];
}

function toIncidents(
  raw: BackendLaboratoryResult["processingIncidents"],
): LaboratoryResult["incidents"] {
  return (raw ?? []).map((incident) => ({
    incidentType: incident.incidentType,
    description: incident.notes ?? "",
    loggedAt: incident.recordedAt,
    loggedBy: incident.recordedBy,
  }));
}

function toAmendments(raw: BackendLaboratoryResult["amendments"]): LaboratoryResult["amendments"] {
  return (raw ?? []).map((amendment) => ({
    reason: amendment.amendmentReason,
    amendedAt: amendment.amendedAt,
    amendedBy: amendment.amendedBy,
  }));
}

/** Normalizes the real BCM-LAB-006 payload into the shared FE `LaboratoryResult` shape. */
export function toLaboratoryResult(raw: BackendLaboratoryResult): LaboratoryResult {
  return {
    resultId: raw.resultId,
    tenantId: raw.tenantId,
    laboratoryId: raw.laboratoryId,
    sampleId: raw.sampleId,
    testDefinitionId: raw.analyteSnapshot?.testDefinitionId ?? "",
    status: raw.status,
    analyteSnapshots: toAnalyteSnapshots(raw.analyteSnapshot),
    referenceRangeSnapshots: toReferenceRangeSnapshots(raw.referenceRangeSnapshot),
    resultValues: toResultValues(raw.resultValue),
    incidents: toIncidents(raw.processingIncidents),
    technicalValidation: raw.technicalValidation ?? undefined,
    medicalValidation: raw.medicalValidation ?? undefined,
    releaseRecord: raw.releaseRecord
      ? { releasedBy: raw.releaseRecord.releasedBy, releasedAt: raw.releaseRecord.releasedAt }
      : undefined,
    amendments: toAmendments(raw.amendments),
    version: 1,
  };
}
