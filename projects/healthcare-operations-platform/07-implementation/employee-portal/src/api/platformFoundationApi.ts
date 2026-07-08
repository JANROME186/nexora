import { get, post } from "./httpClient";
import type {
  AssignRoleRequest,
  AuditEvent,
  AuditEventSearchParams,
  Branch,
  CreateBranchRequest,
  CreateLaboratoryRequest,
  CreateTenantRequest,
  CreateUserRequest,
  Laboratory,
  Tenant,
  UserAccount
} from "./types";

const API_BASE = "/api";

export function createTenant(request: CreateTenantRequest): Promise<Tenant> {
  return post<Tenant, CreateTenantRequest>(`${API_BASE}/platform/tenants`, request);
}

export function getTenant(tenantId: string): Promise<Tenant> {
  return get<Tenant>(`${API_BASE}/platform/tenants/${encodeURIComponent(tenantId)}`);
}

export function createLaboratory(request: CreateLaboratoryRequest): Promise<Laboratory> {
  return post<Laboratory, CreateLaboratoryRequest>(`${API_BASE}/organization/laboratories`, request);
}

export function getLaboratory(laboratoryId: string): Promise<Laboratory> {
  return get<Laboratory>(`${API_BASE}/organization/laboratories/${encodeURIComponent(laboratoryId)}`);
}

export function createBranch(request: CreateBranchRequest): Promise<Branch> {
  return post<Branch, CreateBranchRequest>(`${API_BASE}/organization/branches`, request);
}

export function getBranch(branchId: string): Promise<Branch> {
  return get<Branch>(`${API_BASE}/organization/branches/${encodeURIComponent(branchId)}`);
}

export function createUser(request: CreateUserRequest): Promise<UserAccount> {
  return post<UserAccount, CreateUserRequest>(`${API_BASE}/identity/users`, request);
}

export function getUser(userId: string): Promise<UserAccount> {
  return get<UserAccount>(`${API_BASE}/identity/users/${encodeURIComponent(userId)}`);
}

export function assignRole(userId: string, request: AssignRoleRequest): Promise<void> {
  return post<void, AssignRoleRequest>(
    `${API_BASE}/identity/users/${encodeURIComponent(userId)}/role-assignments`,
    request
  );
}

export function searchAuditEvents(params: AuditEventSearchParams): Promise<AuditEvent[]> {
  const query = new URLSearchParams();
  if (params.tenantId) {
    query.set("tenantId", params.tenantId);
  }
  if (params.subjectId) {
    query.set("subjectId", params.subjectId);
  }
  const queryString = query.toString();
  return get<AuditEvent[]>(`${API_BASE}/audit/events${queryString ? `?${queryString}` : ""}`);
}
