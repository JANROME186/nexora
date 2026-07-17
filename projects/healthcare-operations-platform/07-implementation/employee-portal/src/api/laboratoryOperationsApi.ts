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
  return get(`${BASE_URL}/laboratory-results?tenantId=${tenantId}&worklist=processing`);
}

export async function listTechnicalValidationWorklist(
  tenantId: string,
): Promise<LaboratoryResult[]> {
  return get(`${BASE_URL}/laboratory-results/technical-validation-worklist?tenantId=${tenantId}`);
}

export async function listMedicalValidationWorklist(tenantId: string): Promise<LaboratoryResult[]> {
  return get(`${BASE_URL}/laboratory-results/medical-validation-worklist?tenantId=${tenantId}`);
}

export async function listReleaseWorklist(tenantId: string): Promise<LaboratoryResult[]> {
  return get(`${BASE_URL}/laboratory-results/release-worklist?tenantId=${tenantId}`);
}

export async function getLaboratoryResult(resultId: string): Promise<LaboratoryResult> {
  return get(`${BASE_URL}/laboratory-results/${resultId}`);
}

export async function captureResult(
  resultId: string,
  req: CaptureResultRequest,
): Promise<LaboratoryResult> {
  return post(`${BASE_URL}/laboratory-results/${resultId}/capture`, req);
}

export async function recordIncident(
  resultId: string,
  req: RecordIncidentRequest,
): Promise<LaboratoryResult> {
  return post(`${BASE_URL}/laboratory-results/${resultId}/incident`, req);
}

export async function submitForValidation(resultId: string): Promise<LaboratoryResult> {
  return post(`${BASE_URL}/laboratory-results/${resultId}/submit`, {});
}

export async function validateTechnically(
  resultId: string,
  req: ValidateResultRequest,
): Promise<LaboratoryResult> {
  return post(`${BASE_URL}/laboratory-results/${resultId}/technical-validation`, req);
}

export async function validateMedically(
  resultId: string,
  req: ValidateResultRequest,
): Promise<LaboratoryResult> {
  return post(`${BASE_URL}/laboratory-results/${resultId}/medical-validation`, req);
}

export async function releaseResult(
  resultId: string,
  req: ReleaseResultRequest,
): Promise<LaboratoryResult> {
  return post(`${BASE_URL}/laboratory-results/${resultId}/release`, req);
}

export async function amendResult(
  resultId: string,
  req: AmendResultRequest,
): Promise<LaboratoryResult> {
  return post(`${BASE_URL}/laboratory-results/${resultId}/amend`, req);
}
