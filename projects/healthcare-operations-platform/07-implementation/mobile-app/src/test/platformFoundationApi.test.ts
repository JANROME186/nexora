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
      getSessionHeaders: () => ({
        "X-HOP-USER-ID": "mobile-user",
        "X-HOP-ROLES": "FRONT_DESK",
      }),
    });

    const tenant = await api.createTenant({ name: "Mobile Tenant" });

    expect(tenant.tenantId).toBe("tenant-1");
    expect(calls[0].input).toBe("http://localhost:8080/api/platform/tenants");
    const headers = new Headers(calls[0].init?.headers);
    expect(headers.get("Authorization")).toBe("Bearer local-token");
    expect(headers.get("X-HOP-USER-ID")).toBe("mobile-user");
    expect(headers.get("X-HOP-ROLES")).toBe("FRONT_DESK");
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

  it("covers platform, organization, identity and role-assignment operations", async () => {
    const calls: Array<{ input: string; init?: RequestInit }> = [];
    const fetcher: FetchLike = async (input, init) => {
      calls.push({ input, init });
      if (String(input).includes("/laboratories")) {
        return json({ laboratoryId: "lab-1", tenantId: "tenant-1", name: "Main Lab" });
      }
      if (String(input).includes("/branches")) {
        return json({
          branchId: "branch-1",
          tenantId: "tenant-1",
          laboratoryId: "lab-1",
          name: "North",
        });
      }
      if (String(input).includes("/identity/users/user-1/role-assignments")) {
        return new Response(null, { status: 204 });
      }
      if (String(input).includes("/identity/users")) {
        return json({
          userId: "user-1",
          tenantId: "tenant-1",
          displayName: "Mobile User",
          email: "mobile@example.test",
          status: "active",
        });
      }
      return json({ tenantId: "tenant-1", name: "Tenant" });
    };
    const api = createPlatformFoundationApi({ baseUrl: "http://localhost:8080", fetcher });

    await api.getTenant("tenant-1");
    await api.createLaboratory({ tenantId: "tenant-1", name: "Main Lab" });
    await api.getLaboratory("lab-1");
    await api.createBranch({ laboratoryId: "lab-1", name: "North" });
    await api.getBranch("branch-1");
    await api.createUser({
      tenantId: "tenant-1",
      displayName: "Mobile User",
      email: "mobile@example.test",
    });
    await api.getUser("user-1");
    await api.assignRole("user-1", {
      roleCode: "FRONT_DESK",
      scope: { type: "tenant", id: "tenant-1" },
    });

    expect(calls.map((call) => call.input)).toContain(
      "http://localhost:8080/api/identity/users/user-1/role-assignments",
    );
    expect(calls.at(-1)?.init?.method).toBe("POST");
  });

  it("throws when backend responds with a non-success status", async () => {
    const fetcher: FetchLike = async () => new Response("Forbidden", { status: 403 });
    const api = createPlatformFoundationApi({ baseUrl: "http://localhost:8080", fetcher });

    await expect(api.getTenant("tenant-1")).rejects.toThrow(
      "Platform Foundation API request failed with status 403.",
    );
  });
});

function json(payload: unknown) {
  return new Response(JSON.stringify(payload), {
    status: 200,
    headers: { "Content-Type": "application/json" },
  });
}
