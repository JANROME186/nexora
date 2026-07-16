import { describe, expect, it } from "vitest";

import { createPlatformFoundationApi } from "../api/platformFoundationApi";
import type { FetchLike } from "../api/platformFoundationApi";

describe("platform foundation mobile api client", () => {
  it("sends bearer token and creates tenant through the backend API", async () => {
    const calls: Array<{ input: string; init?: RequestInit }> = [];
    const fetcher: FetchLike = async (input, init) => {
      calls.push({ input, init });
      return new Response(
        JSON.stringify({ tenantId: "tenant-1", name: "Mobile Tenant", status: "active" }),
        {
          status: 201,
          headers: { "Content-Type": "application/json" },
        },
      );
    };
    const api = createPlatformFoundationApi({
      baseUrl: "http://localhost:8080",
      fetcher,
      getToken: () => "local-token",
    });

    const tenant = await api.createTenant({ name: "Mobile Tenant" });

    expect(tenant.tenantId).toBe("tenant-1");
    expect(calls[0].input).toBe("http://localhost:8080/api/platform/tenants");
    expect(new Headers(calls[0].init?.headers).get("Authorization")).toBe("Bearer local-token");
  });

  it("builds audit search query parameters", async () => {
    let requestedUrl = "";
    const fetcher: FetchLike = async (input) => {
      requestedUrl = input;
      return new Response(JSON.stringify([]), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      });
    };
    const api = createPlatformFoundationApi({ baseUrl: "http://localhost:8080/", fetcher });

    await api.searchAuditEvents({ tenantId: "tenant-1", subjectId: "subject-1" });

    expect(requestedUrl).toBe(
      "http://localhost:8080/api/audit/events?tenantId=tenant-1&subjectId=subject-1",
    );
  });
});
