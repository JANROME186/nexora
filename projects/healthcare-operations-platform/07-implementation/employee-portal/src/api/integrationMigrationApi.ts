/**
 * Integration and Migration Readiness API client (MVP-MOD-008-FE-001).
 *
 * Covers employee-portal administration endpoints generated from:
 *   BCM-PLT-004 Integration Management
 *   BCM-PLT-005 API Management
 *   BCM-PLT-010 Open Data Ingestion and Migration
 *
 * BCM-PLT-005 is also the TD-STACK-003 TypeScript client pilot target. This file keeps a
 * generated-client-shaped facade over the hand-written HTTP adapter so screens depend on a typed
 * operation surface that can be replaced by OpenAPI Generator output without changing UI code.
 */
import { get, post, postForm, put } from "./httpClient";
import type {
  ApiSurfaceRegistration,
  ClassifyApiOperationRequest,
  CreateMigrationJobRequest,
  DryRunReport,
  ImportBatch,
  ImportExecution,
  IntegrationEndpoint,
  IntegrationMessage,
  IntegrationMessageDetail,
  IssuePartnerApiKeyRequest,
  MigrationJob,
  MigrationJobStatus,
  PartnerApiKey,
  RateLimitPolicy,
  ReceiveIntegrationMessageRequest,
  ReconciliationReport,
  RegisterIntegrationEndpointRequest,
  RetryIntegrationMessageRequest,
  ScheduleApiDeprecationRequest,
  SetRateLimitPolicyRequest,
} from "./types";

const INTEGRATION_ENDPOINTS_BASE = "/api/platform/integration/endpoints";
const INTEGRATION_MESSAGES_BASE = "/api/platform/integration/messages";
const API_OPERATIONS_BASE = "/api/platform/api-management/operations";
const API_KEYS_BASE = "/api/platform/api-management/partner-keys";
const RATE_LIMIT_BASE = "/api/platform/api-management/rate-limit-policies";
const MIGRATION_JOBS_BASE = "/api/platform/migration/jobs";
const IMPORT_BATCHES_BASE = "/api/platform/migration/import-batches";

function encode(value: string): string {
  return encodeURIComponent(value);
}

export function listIntegrationEndpoints(tenantId: string): Promise<IntegrationEndpoint[]> {
  return get(`${INTEGRATION_ENDPOINTS_BASE}?tenantId=${encode(tenantId)}`);
}

export function registerIntegrationEndpoint(
  request: RegisterIntegrationEndpointRequest,
): Promise<IntegrationEndpoint> {
  return post(INTEGRATION_ENDPOINTS_BASE, request);
}

export function getIntegrationEndpoint(endpointId: string): Promise<IntegrationEndpoint> {
  return get(`${INTEGRATION_ENDPOINTS_BASE}/${encode(endpointId)}`);
}

export function retireIntegrationEndpoint(
  endpointId: string,
  actorId: string,
): Promise<IntegrationEndpoint> {
  return post(`${INTEGRATION_ENDPOINTS_BASE}/${encode(endpointId)}/retire`, { actorId });
}

export function receiveIntegrationMessage(
  endpointId: string,
  request: ReceiveIntegrationMessageRequest,
): Promise<IntegrationMessage> {
  return post(`${INTEGRATION_ENDPOINTS_BASE}/${encode(endpointId)}/messages`, request);
}

export function getIntegrationMessage(messageId: string): Promise<IntegrationMessageDetail> {
  return get(`${INTEGRATION_MESSAGES_BASE}/${encode(messageId)}`);
}

export function retryIntegrationMessage(
  messageId: string,
  request: RetryIntegrationMessageRequest,
): Promise<IntegrationMessageDetail> {
  return post(`${INTEGRATION_MESSAGES_BASE}/${encode(messageId)}/retry`, request);
}

export function listApiOperations(): Promise<ApiSurfaceRegistration[]> {
  return get(API_OPERATIONS_BASE);
}

export function classifyApiOperation(
  operationId: string,
  request: ClassifyApiOperationRequest,
): Promise<ApiSurfaceRegistration> {
  return post(`${API_OPERATIONS_BASE}/${encode(operationId)}/classification`, request);
}

export function scheduleApiDeprecation(
  operationId: string,
  request: ScheduleApiDeprecationRequest,
): Promise<ApiSurfaceRegistration> {
  return post(`${API_OPERATIONS_BASE}/${encode(operationId)}/deprecation`, request);
}

export function retireApiOperation(
  operationId: string,
  actorId: string,
): Promise<ApiSurfaceRegistration> {
  return post(`${API_OPERATIONS_BASE}/${encode(operationId)}/retirement`, { actorId });
}

export function listPartnerApiKeys(tenantId: string): Promise<PartnerApiKey[]> {
  return get(`${API_KEYS_BASE}?tenantId=${encode(tenantId)}`);
}

export function issuePartnerApiKey(request: IssuePartnerApiKeyRequest): Promise<PartnerApiKey> {
  return post(API_KEYS_BASE, request);
}

export function revokePartnerApiKey(keyId: string, actorId: string): Promise<PartnerApiKey> {
  return post(`${API_KEYS_BASE}/${encode(keyId)}/revoke`, { actorId });
}

export function setRateLimitPolicy(
  classification: string,
  request: SetRateLimitPolicyRequest,
): Promise<RateLimitPolicy> {
  return put(`${RATE_LIMIT_BASE}/${encode(classification)}`, request);
}

export function listMigrationJobs(tenantId: string): Promise<MigrationJob[]> {
  return get(`${MIGRATION_JOBS_BASE}?tenantId=${encode(tenantId)}`);
}

export function createMigrationJob(request: CreateMigrationJobRequest): Promise<MigrationJob> {
  return post(MIGRATION_JOBS_BASE, request);
}

export function getMigrationJob(migrationJobId: string): Promise<MigrationJob> {
  return get(`${MIGRATION_JOBS_BASE}/${encode(migrationJobId)}`);
}

export function receiveImportPackage(
  migrationJobId: string,
  manifest: File,
  packageFile: File,
  zipBundle: boolean,
  actorId: string,
): Promise<ImportBatch> {
  const body = new FormData();
  body.append("manifest", manifest);
  body.append("package", packageFile);
  return postForm(
    `${MIGRATION_JOBS_BASE}/${encode(migrationJobId)}/import-batches?zipBundle=${zipBundle}&actorId=${encode(actorId)}`,
    body,
  );
}

export function retryImportExecution(
  migrationJobId: string,
  actorId: string,
): Promise<ImportExecution> {
  return post(`${MIGRATION_JOBS_BASE}/${encode(migrationJobId)}/retry`, { actorId });
}

export function listReconciliationReports(migrationJobId: string): Promise<ReconciliationReport[]> {
  return get(`${MIGRATION_JOBS_BASE}/${encode(migrationJobId)}/reconciliation`);
}

export function runDryRunValidation(importBatchId: string, actorId: string): Promise<DryRunReport> {
  return post(`${IMPORT_BATCHES_BASE}/${encode(importBatchId)}/dry-run`, { actorId });
}

export function getDryRunReport(importBatchId: string): Promise<DryRunReport> {
  return get(`${IMPORT_BATCHES_BASE}/${encode(importBatchId)}/dry-run`);
}

export function approveImport(importBatchId: string, actorId: string): Promise<MigrationJobStatus> {
  return post(`${IMPORT_BATCHES_BASE}/${encode(importBatchId)}/approve`, { actorId });
}

export function commitImport(importBatchId: string, actorId: string): Promise<ImportExecution> {
  return post(`${IMPORT_BATCHES_BASE}/${encode(importBatchId)}/commit`, { actorId });
}
