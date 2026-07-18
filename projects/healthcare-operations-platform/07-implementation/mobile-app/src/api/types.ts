export type TenantResponse = {
  tenantId: string;
  name: string;
  status?: string;
};

export type LaboratoryResponse = {
  laboratoryId: string;
  tenantId: string;
  name: string;
  status?: string;
};

export type BranchResponse = {
  branchId: string;
  tenantId: string;
  laboratoryId: string;
  name: string;
  status?: string;
};

export type UserResponse = {
  userId: string;
  tenantId: string;
  displayName: string;
  email: string;
  status: "created" | "active" | "locked" | "suspended";
};

export type AccessScope = {
  type: "platform" | "tenant" | "laboratory" | "branch";
  id: string;
};

export type AuditEventResponse = {
  auditEventId: string;
  occurredAt: string;
  tenantId?: string;
  actorId: string;
  actorType: string;
  action: string;
  subjectType: string;
  subjectId: string;
  metadataJson: string;
};

export type CreateTenantRequest = {
  name: string;
};

export type CreateLaboratoryRequest = {
  tenantId: string;
  name: string;
};

export type CreateBranchRequest = {
  laboratoryId: string;
  name: string;
};

export type CreateUserRequest = {
  tenantId: string;
  displayName: string;
  email: string;
};

export type AssignRoleRequest = {
  roleCode: string;
  scope: AccessScope;
};

export type DeliveryTicketResponse = {
  deliveryTicketId: string;
  patientId: string;
  orderId: string;
  status: "pending" | "delivered" | "viewed";
  releasedAt: string;
  viewedAt?: string;
  reportUrl?: string;
};

export type ResultSummaryResponse = {
  resultId: string;
  testName: string;
  status: string;
  value: string;
  referenceRange?: string;
  isAbnormal: boolean;
  releasedAt: string;
};

export type ResultHistoryResponse = {
  patientId: string;
  testId: string;
  testName: string;
  entries: {
    resultId: string;
    value: string;
    isAbnormal: boolean;
    releasedAt: string;
  }[];
};
