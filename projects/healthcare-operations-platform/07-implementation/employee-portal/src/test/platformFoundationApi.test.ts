import { afterEach, describe, expect, it, vi } from "vitest";
import { createTenant, getTenant } from "../api/platformFoundationApi";
import { ApiError } from "../api/httpClient";

function mockFetchOnce(response: Partial<Response> & { jsonBody?: unknown }) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok: response.ok ?? true,
    status: response.status ?? 200,
    statusText: response.statusText ?? "OK",
    json: async () => response.jsonBody
  } as Response);
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

describe("platformFoundationApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("creates a tenant by posting to the platform tenants endpoint", async () => {
    const fetchMock = mockFetchOnce({
      status: 201,
      jsonBody: { tenantId: "tenant-1", name: "Nexora Diagnostics", status: "active" }
    });

    const tenant = await createTenant({ name: "Nexora Diagnostics" });

    expect(tenant).toEqual({ tenantId: "tenant-1", name: "Nexora Diagnostics", status: "active" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/platform/tenants",
      expect.objectContaining({ method: "POST" })
    );
  });

  it("throws an ApiError with the backend message when the tenant is not found", async () => {
    mockFetchOnce({
      ok: false,
      status: 404,
      statusText: "Not Found",
      jsonBody: { status: 404, message: "Tenant was not found." }
    });

    await expect(getTenant("missing-tenant")).rejects.toMatchObject({
      message: "Tenant was not found.",
      status: 404
    } satisfies Partial<ApiError>);
  });
});
