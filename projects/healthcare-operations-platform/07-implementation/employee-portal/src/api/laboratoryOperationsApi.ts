import { get, post } from "./httpClient";
import type {
  Sample,
  CollectSampleRequest,
  ReceiveSampleRequest,
  RejectSampleRequest,
  DisposeSampleRequest,
  LaboratoryResult,
  CaptureResultRequest,
  RecordIncidentRequest,
  ValidateResultRequest,
  ReleaseResultRequest,
  AmendResultRequest,
} from "./types";
import { toLaboratoryResult, type BackendLaboratoryResult } from "./laboratoryResultMapper";

const BASE_URL = "/api/clinical-operations";

// --- Samples ---

export async function listCollectionWorklist(tenantId: string): Promise<Sample[]> {
  return get(`${BASE_URL}/samples?tenantId=${tenantId}&worklist=collection`);
}

export async function listReceptionWorklist(tenantId: string): Promise<Sample[]> {
  return get(`${BASE_URL}/samples?tenantId=${tenantId}&worklist=reception`);
}

export async function getSample(sampleId: string): Promise<Sample> {
  return get(`${BASE_URL}/samples/${sampleId}`);
}

export async function collectSample(sampleId: string, req: CollectSampleRequest): Promise<Sample> {
  return post(`${BASE_URL}/samples/${sampleId}/collect`, req);
}

export async function receiveSample(sampleId: string, req: ReceiveSampleRequest): Promise<Sample> {
  return post(`${BASE_URL}/samples/${sampleId}/reception`, req);
}

export async function rejectSample(sampleId: string, req: RejectSampleRequest): Promise<Sample> {
  return post(`${BASE_URL}/samples/${sampleId}/reject`, req);
}

export async function disposeSample(sampleId: string, req: DisposeSampleRequest): Promise<Sample> {
  return post(`${BASE_URL}/samples/${sampleId}/dispose`, req);
}

export async function getLabelPrintJob(sampleId: string): Promise<unknown> {
  return get(`${BASE_URL}/samples/${sampleId}/label`);
}

// --- Laboratory Results ---

export async function listProcessingWorklist(tenantId: string): Promise<LaboratoryResult[]> {
  const raw = await get<BackendLaboratoryResult[]>(
    `${BASE_URL}/laboratory-results?tenantId=${tenantId}&worklist=processing`,
  );
  return raw.map(toLaboratoryResult);
}

export async function listTechnicalValidationWorklist(
  tenantId: string,
): Promise<LaboratoryResult[]> {
  const raw = await get<BackendLaboratoryResult[]>(
    `${BASE_URL}/laboratory-results/technical-validation-worklist?tenantId=${tenantId}`,
  );
  return raw.map(toLaboratoryResult);
}

export async function listMedicalValidationWorklist(tenantId: string): Promise<LaboratoryResult[]> {
  const raw = await get<BackendLaboratoryResult[]>(
    `${BASE_URL}/laboratory-results/medical-validation-worklist?tenantId=${tenantId}`,
  );
  return raw.map(toLaboratoryResult);
}

export async function listReleaseWorklist(tenantId: string): Promise<LaboratoryResult[]> {
  const raw = await get<BackendLaboratoryResult[]>(
    `${BASE_URL}/laboratory-results/release-worklist?tenantId=${tenantId}`,
  );
  return raw.map(toLaboratoryResult);
}

export async function getLaboratoryResult(resultId: string): Promise<LaboratoryResult> {
  const raw = await get<BackendLaboratoryResult>(`${BASE_URL}/laboratory-results/${resultId}`);
  return toLaboratoryResult(raw);
}

export async function captureResult(
  resultId: string,
  req: CaptureResultRequest,
): Promise<LaboratoryResult> {
  const raw = await post<BackendLaboratoryResult>(
    `${BASE_URL}/laboratory-results/${resultId}/capture`,
    req,
  );
  return toLaboratoryResult(raw);
}

export async function recordIncident(
  resultId: string,
  req: RecordIncidentRequest,
): Promise<LaboratoryResult> {
  const raw = await post<BackendLaboratoryResult>(
    `${BASE_URL}/laboratory-results/${resultId}/incident`,
    req,
  );
  return toLaboratoryResult(raw);
}

export async function submitForValidation(resultId: string): Promise<LaboratoryResult> {
  const raw = await post<BackendLaboratoryResult>(
    `${BASE_URL}/laboratory-results/${resultId}/submit`,
    {},
  );
  return toLaboratoryResult(raw);
}

export async function validateTechnically(
  resultId: string,
  req: ValidateResultRequest,
): Promise<LaboratoryResult> {
  const raw = await post<BackendLaboratoryResult>(
    `${BASE_URL}/laboratory-results/${resultId}/technical-validation`,
    req,
  );
  return toLaboratoryResult(raw);
}

export async function validateMedically(
  resultId: string,
  req: ValidateResultRequest,
): Promise<LaboratoryResult> {
  const raw = await post<BackendLaboratoryResult>(
    `${BASE_URL}/laboratory-results/${resultId}/medical-validation`,
    req,
  );
  return toLaboratoryResult(raw);
}

export async function releaseResult(
  resultId: string,
  req: ReleaseResultRequest,
): Promise<LaboratoryResult> {
  const raw = await post<BackendLaboratoryResult>(
    `${BASE_URL}/laboratory-results/${resultId}/release`,
    req,
  );
  return toLaboratoryResult(raw);
}

export async function amendResult(
  resultId: string,
  req: AmendResultRequest,
): Promise<LaboratoryResult> {
  const raw = await post<BackendLaboratoryResult>(
    `${BASE_URL}/laboratory-results/${resultId}/amend`,
    req,
  );
  return toLaboratoryResult(raw);
}
