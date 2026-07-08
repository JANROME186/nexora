export type AccessScopeType = "platform" | "tenant" | "laboratory" | "branch";

export interface AccessScope {
  type: AccessScopeType;
  id: string;
}

export interface Tenant {
  tenantId: string;
  name: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateTenantRequest {
  name: string;
}

export interface Laboratory {
  laboratoryId: string;
  tenantId: string;
  name: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateLaboratoryRequest {
  tenantId: string;
  name: string;
}

export interface Branch {
  branchId: string;
  tenantId: string;
  laboratoryId: string;
  name: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateBranchRequest {
  laboratoryId: string;
  name: string;
}

export type UserStatus = "created" | "active" | "locked" | "suspended";

export interface UserAccount {
  userId: string;
  tenantId: string;
  displayName: string;
  email: string;
  status: UserStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateUserRequest {
  tenantId: string;
  displayName: string;
  email: string;
}

export interface AssignRoleRequest {
  roleCode: string;
  scope: AccessScope;
}

export interface AuditEvent {
  auditEventId: string;
  occurredAt: string;
  tenantId?: string;
  actorId: string;
  actorType: string;
  action: string;
  subjectType: string;
  subjectId: string;
  metadataJson?: string;
}

export interface AuditEventSearchParams {
  tenantId?: string;
  subjectId?: string;
}
