import { describe, expect, it } from "vitest";

import { createPatientMobileApi } from "../api/patientMobileApi";
import type { FetchLike } from "../api/platformFoundationApi";

describe("patientMobileApi", () => {
  it("builds patient workflow URLs and forwards auth/session headers", async () => {
    const calls: Array<{ input: string; init?: RequestInit }> = [];
    const fetcher: FetchLike = async (input, init) => {
      calls.push({ input, init });
      return new Response(JSON.stringify([]), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      });
    };
    const api = createPatientMobileApi({
      baseUrl: "http://localhost:8080/",
      fetcher,
      getToken: () => "token-1",
      getSessionHeaders: () => ({ "X-Tenant-Id": "tenant-1" }),
    });

    await api.listAppointments("patient 1");
    await api.listOrders("patient-1");
    await api.listResults("patient-1");
    await api.listNotifications("patient-1");

    expect(calls.map((call) => call.input)).toEqual([
      "http://localhost:8080/api/portal/patient/appointments?patientId=patient+1",
      "http://localhost:8080/api/clinical-operations/diagnostic-orders?patientId=patient-1",
      "http://localhost:8080/api/delivery/results?patientId=patient-1",
      "http://localhost:8080/api/portal/patient/notifications?patientId=patient-1",
    ]);
    const headers = calls[0].init?.headers as Headers;
    expect(headers.get("Authorization")).toBe("Bearer token-1");
    expect(headers.get("X-Tenant-Id")).toBe("tenant-1");
  });

  it("fetches profile by encoded patient id and reports HTTP failures", async () => {
    const calls: string[] = [];
    const fetcher: FetchLike = async (input) => {
      calls.push(String(input));
      return new Response("Denied", { status: 403 });
    };
    const api = createPatientMobileApi({ baseUrl: "https://api.test", fetcher });

    await expect(api.getProfile("patient/1")).rejects.toThrow(
      "Patient mobile API request failed with status 403.",
    );
    expect(calls[0]).toBe("https://api.test/api/portal/patient/profile/patient%2F1");
  });
});
