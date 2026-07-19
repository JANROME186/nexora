import { afterEach, describe, expect, it, vi } from "vitest";
import {
  classifyApiOperation,
  listIntegrationEndpoints,
  receiveImportPackage,
  retryIntegrationMessage,
  setRateLimitPolicy,
} from "../api/integrationMigrationApi";

function mockFetchOnce(response: Partial<Response> & { jsonBody?: unknown }) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok: response.ok ?? true,
    status: response.status ?? 200,
    statusText: response.statusText ?? "OK",
    json: async () => response.jsonBody,
  } as Response);
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

describe("integrationMigrationApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    window.localStorage.clear();
  });

  it("lists integration endpoints for the tenant", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: [{ endpointId: "endpoint-1", tenantId: "tenant-1" }],
    });

    const endpoints = await listIntegrationEndpoints("tenant 1");

    expect(endpoints).toEqual([{ endpointId: "endpoint-1", tenantId: "tenant-1" }]);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/platform/integration/endpoints?tenantId=tenant%201",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("classifies API operations through the BCM-PLT-005 operation contract", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: {
        registrationId: "reg-1",
        operationId: "op-1",
        classification: "partner",
      },
    });

    await classifyApiOperation("op-1", {
      ownerCapability: "BCM-PLT-005",
      classification: "partner",
      apiVersion: "v1",
      tenantId: "tenant-1",
      actorId: "user-1",
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/platform/api-management/operations/op-1/classification",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({
          ownerCapability: "BCM-PLT-005",
          classification: "partner",
          apiVersion: "v1",
          tenantId: "tenant-1",
          actorId: "user-1",
        }),
      }),
    );
  });

  it("retries integration messages with actor and replacement payload", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { messageId: "msg-1", retryCount: 2 },
    });

    await retryIntegrationMessage("msg-1", { rawPayload: "MSH|A", actorId: "user-1" });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/platform/integration/messages/msg-1/retry",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ rawPayload: "MSH|A", actorId: "user-1" }),
      }),
    );
  });

  it("sets rate-limit policy through PUT", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { policyId: "policy-1", classification: "partner", requestsPerMinute: 120 },
    });

    await setRateLimitPolicy("partner", { requestsPerMinute: 120, actorId: "user-1" });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/platform/api-management/rate-limit-policies/partner",
      expect.objectContaining({
        method: "PUT",
        body: JSON.stringify({ requestsPerMinute: 120, actorId: "user-1" }),
      }),
    );
  });

  it("uploads migration import packages as multipart form data", async () => {
    const fetchMock = mockFetchOnce({
      status: 201,
      jsonBody: { importBatchId: "batch-1", migrationJobId: "job-1", entityCounts: {} },
    });
    const manifest = new File(["manifest"], "manifest.json", { type: "application/json" });
    const packageFile = new File(["patientId,name\n1,Ada"], "patients.csv", { type: "text/csv" });

    await receiveImportPackage("job-1", manifest, packageFile, false, "user-1");

    const [, init] = fetchMock.mock.calls[0];
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/platform/migration/jobs/job-1/import-batches?zipBundle=false&actorId=user-1",
      expect.objectContaining({ method: "POST" }),
    );
    expect(init.body).toBeInstanceOf(FormData);
    expect(init.headers).not.toHaveProperty("Content-Type");
  });
});
