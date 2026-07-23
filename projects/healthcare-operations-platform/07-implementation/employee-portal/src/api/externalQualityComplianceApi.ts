/**
 * Advanced Quality and Compliance API client (COM-MOD-013-FE-001).
 *
 * Covers employee-portal administration endpoints generated from:
 *   BCM-QLT-002 External Quality Controls  /api/quality/external-controls
 *   BCM-QLT-006 CAPA Management            /api/quality/capa
 *   BCM-QLT-007 Audit Management           /api/quality/audits
 *   BCM-PLT-007 Compliance Evidence Export /api/audit/events/export
 *   BCM-PLT-008 Document Management        /api/documents
 *   Quality Event Intake                   /api/quality/events/intake
 *
 * Mirrors the `integrationMigrationApi.ts` shape (TD-STACK-003): a thin, typed operation facade
 * over the hand-written HTTP adapter, one function per backend endpoint. Backend quality logic
 * (CAPA lifecycle, audit finding validation, compliance retention) is never reimplemented here.
 */
import { get, post, put } from "./httpClient";
import type {
  AssignCapaRequest,
  CapaRecord,
  CloseCapaRequest,
  CloseQualityAuditRequest,
  ComplianceEvidenceExport,
  CreateExternalQCRequest,
  ExportComplianceEvidenceRequest,
  ExternalQualityControl,
  LinkQualityEventRequest,
  OpenCapaRequest,
  OpenQualityAuditRequest,
  PlanQualityAuditRequest,
  QualityAudit,
  QualityEvent,
  RecordFindingRequest,
  RecordQualityEventRequest,
  ReviewExternalQCRequest,
  SearchComplianceEvidenceParams,
  SearchDocumentsParams,
  StoredDocument,
  VerifyCapaRequest,
} from "./types";

const EXTERNAL_QC_BASE = "/api/quality/external-controls";
const CAPA_BASE = "/api/quality/capa";
const AUDITS_BASE = "/api/quality/audits";
const COMPLIANCE_EXPORT_BASE = "/api/audit/events/export";
const AUDIT_EVENTS_BASE = "/api/audit/events";
const DOCUMENTS_BASE = "/api/documents";
const QUALITY_EVENTS_BASE = "/api/quality/events/intake";

function encode(value: string): string {
  return encodeURIComponent(value);
}

function scopeQuery(tenantId: string, laboratoryId: string): string {
  return `tenantId=${encode(tenantId)}&laboratoryId=${encode(laboratoryId)}`;
}

// -- BCM-QLT-002 External Quality Controls ------------------------------------------------

export function listExternalQualityControls(
  tenantId: string,
  laboratoryId: string,
): Promise<ExternalQualityControl[]> {
  return get<ExternalQualityControl[]>(`${EXTERNAL_QC_BASE}?${scopeQuery(tenantId, laboratoryId)}`);
}

export function createExternalQualityControl(
  tenantId: string,
  laboratoryId: string,
  request: CreateExternalQCRequest,
): Promise<ExternalQualityControl> {
  return post<ExternalQualityControl>(
    `${EXTERNAL_QC_BASE}?${scopeQuery(tenantId, laboratoryId)}`,
    request,
  );
}

export function approveExternalQualityControl(
  externalQCId: string,
  request: ReviewExternalQCRequest,
): Promise<ExternalQualityControl> {
  return put<ExternalQualityControl>(
    `${EXTERNAL_QC_BASE}/${encode(externalQCId)}/approve`,
    request,
  );
}

export function rejectExternalQualityControl(
  externalQCId: string,
  request: ReviewExternalQCRequest,
): Promise<ExternalQualityControl> {
  return put<ExternalQualityControl>(`${EXTERNAL_QC_BASE}/${encode(externalQCId)}/reject`, request);
}

// -- BCM-QLT-006 CAPA Management ----------------------------------------------------------

export function listCapaRecords(tenantId: string, laboratoryId: string): Promise<CapaRecord[]> {
  return get<CapaRecord[]>(`${CAPA_BASE}?${scopeQuery(tenantId, laboratoryId)}`);
}

export function openCapa(
  tenantId: string,
  laboratoryId: string,
  request: OpenCapaRequest,
): Promise<CapaRecord> {
  return post<CapaRecord>(`${CAPA_BASE}?${scopeQuery(tenantId, laboratoryId)}`, request);
}

export function assignCapa(capaId: string, request: AssignCapaRequest): Promise<CapaRecord> {
  return put<CapaRecord>(`${CAPA_BASE}/${encode(capaId)}/assign`, request);
}

export function closeCapa(capaId: string, request: CloseCapaRequest): Promise<CapaRecord> {
  return put<CapaRecord>(`${CAPA_BASE}/${encode(capaId)}/close`, request);
}

export function verifyCapa(capaId: string, request: VerifyCapaRequest): Promise<CapaRecord> {
  return put<CapaRecord>(`${CAPA_BASE}/${encode(capaId)}/verify`, request);
}

// -- BCM-QLT-007 Audit Management ---------------------------------------------------------

export function listQualityAudits(tenantId: string, laboratoryId: string): Promise<QualityAudit[]> {
  return get<QualityAudit[]>(`${AUDITS_BASE}?${scopeQuery(tenantId, laboratoryId)}`);
}

export function planQualityAudit(
  tenantId: string,
  laboratoryId: string,
  request: PlanQualityAuditRequest,
): Promise<QualityAudit> {
  return post<QualityAudit>(`${AUDITS_BASE}?${scopeQuery(tenantId, laboratoryId)}`, request);
}

export function openQualityAudit(
  auditId: string,
  request: OpenQualityAuditRequest,
): Promise<QualityAudit> {
  return put<QualityAudit>(`${AUDITS_BASE}/${encode(auditId)}/open`, request);
}

export function recordAuditFinding(
  auditId: string,
  request: RecordFindingRequest,
): Promise<QualityAudit> {
  return post<QualityAudit>(`${AUDITS_BASE}/${encode(auditId)}/findings`, request);
}

export function closeQualityAudit(
  auditId: string,
  request: CloseQualityAuditRequest,
): Promise<QualityAudit> {
  return put<QualityAudit>(`${AUDITS_BASE}/${encode(auditId)}/close`, request);
}

// -- BCM-PLT-007 Compliance Evidence Export / Search --------------------------------------

export function searchComplianceEvidence(
  params: SearchComplianceEvidenceParams,
): Promise<ComplianceEvidenceExport[]> {
  const qs = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== "")
    .map(([k, v]) => `${encode(k)}=${encode(String(v))}`)
    .join("&");
  const query = qs ? `?${qs}` : "";
  return get<ComplianceEvidenceExport[]>(`${AUDIT_EVENTS_BASE}${query}`);
}

export function exportComplianceEvidence(
  request: ExportComplianceEvidenceRequest,
): Promise<ComplianceEvidenceExport> {
  return post<ComplianceEvidenceExport>(COMPLIANCE_EXPORT_BASE, request);
}

// -- BCM-PLT-008 Document Management (compliance retention search) ------------------------

export function searchDocuments(params: SearchDocumentsParams): Promise<StoredDocument[]> {
  const qs = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== "")
    .map(([k, v]) => `${encode(k)}=${encode(String(v))}`)
    .join("&");
  const query = qs ? `?${qs}` : "";
  return get<StoredDocument[]>(`${DOCUMENTS_BASE}${query}`);
}

// -- Quality Event Intake -----------------------------------------------------------------

export function listQualityEvents(tenantId: string, laboratoryId: string): Promise<QualityEvent[]> {
  return get<QualityEvent[]>(`${QUALITY_EVENTS_BASE}?${scopeQuery(tenantId, laboratoryId)}`);
}

export function recordQualityEvent(
  tenantId: string,
  laboratoryId: string,
  request: RecordQualityEventRequest,
): Promise<QualityEvent> {
  return post<QualityEvent>(
    `${QUALITY_EVENTS_BASE}?${scopeQuery(tenantId, laboratoryId)}`,
    request,
  );
}

export function linkQualityEvent(
  qualityEventId: string,
  request: LinkQualityEventRequest,
): Promise<QualityEvent> {
  return put<QualityEvent>(`${QUALITY_EVENTS_BASE}/${encode(qualityEventId)}/link`, request);
}
