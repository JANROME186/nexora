import type {
  AssignRoleRequest,
  AuditEventResponse,
  BranchResponse,
  CreateBranchRequest,
  CreateLaboratoryRequest,
  CreateTenantRequest,
  CreateUserRequest,
  LaboratoryResponse,
  TenantResponse,
  UserResponse,
} from "./types";

export type FetchLike = (input: string, init?: RequestInit) => Promise<Response>;

export type PlatformFoundationApiOptions = {
  baseUrl: string;
  fetcher?: FetchLike;
  getToken?: () => string | null;
};

export type PlatformFoundationApi = ReturnType<typeof createPlatformFoundationApi>;

export function createPlatformFoundationApi(options: PlatformFoundationApiOptions) {
  const fetcher = options.fetcher ?? fetch;
  const baseUrl = options.baseUrl.replace(/\/$/, "");

  async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
    const token = options.getToken?.();
    const headers = new Headers(init.headers);
    headers.set("Accept", "application/json");
    if (init.body !== undefined) {
      headers.set("Content-Type", "application/json");
    }
    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetcher(`${baseUrl}${path}`, { ...init, headers });
    if (!response.ok) {
      throw new Error(`Platform Foundation API request failed with status ${response.status}.`);
    }
    if (response.status === 204) {
      return undefined as T;
    }
    return response.json() as Promise<T>;
  }

  return {
    createTenant: (payload: CreateTenantRequest) =>
      request<TenantResponse>("/api/platform/tenants", {
        method: "POST",
        body: JSON.stringify(payload),
      }),
    getTenant: (tenantId: string) =>
      request<TenantResponse>(`/api/platform/tenants/${encodeURIComponent(tenantId)}`),
    createLaboratory: (payload: CreateLaboratoryRequest) =>
      request<LaboratoryResponse>("/api/organization/laboratories", {
        method: "POST",
        body: JSON.stringify(payload),
      }),
    getLaboratory: (laboratoryId: string) =>
      request<LaboratoryResponse>(
        `/api/organization/laboratories/${encodeURIComponent(laboratoryId)}`,
      ),
    createBranch: (payload: CreateBranchRequest) =>
      request<BranchResponse>("/api/organization/branches", {
        method: "POST",
        body: JSON.stringify(payload),
      }),
    getBranch: (branchId: string) =>
      request<BranchResponse>(`/api/organization/branches/${encodeURIComponent(branchId)}`),
    createUser: (payload: CreateUserRequest) =>
      request<UserResponse>("/api/identity/users", {
        method: "POST",
        body: JSON.stringify(payload),
      }),
    getUser: (userId: string) =>
      request<UserResponse>(`/api/identity/users/${encodeURIComponent(userId)}`),
    assignRole: (userId: string, payload: AssignRoleRequest) =>
      request<void>(`/api/identity/users/${encodeURIComponent(userId)}/role-assignments`, {
        method: "POST",
        body: JSON.stringify(payload),
      }),
    searchAuditEvents: (filters: { tenantId?: string; subjectId?: string } = {}) => {
      const params = new URLSearchParams();
      if (filters.tenantId) {
        params.set("tenantId", filters.tenantId);
      }
      if (filters.subjectId) {
        params.set("subjectId", filters.subjectId);
      }
      const query = params.toString();
      const querySuffix = query ? `?${query}` : "";
      return request<AuditEventResponse[]>(`/api/audit/events${querySuffix}`);
    },
  };
}
