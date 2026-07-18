import { describe, expect, it } from "vitest";
import { createResultsApi } from "../api/resultsApi";
import type { FetchLike } from "../api/platformFoundationApi";

describe("results mobile api client", () => {
  it("builds correct urls for listing and fetching detail", async () => {
    const calls: Array<{ input: string; init?: RequestInit }> = [];
    const fetcher: FetchLike = async (input, init) => {
      calls.push({ input, init });
      if (String(input).includes("/views")) {
        return new Response(null, { status: 204 });
      }
      return new Response(JSON.stringify([]), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      });
    };
    const api = createResultsApi({ baseUrl: "http://localhost:8080", fetcher });

    await api.listResults("patient-1");
    await api.getResultDetail("ticket-1");
    await api.recordResultViewed("ticket-1");
    await api.getResultHistory("patient-1", "test-1");
    await api.getResultHistory("patient-1");

    expect(calls[0].input).toBe("http://localhost:8080/api/delivery/results?patientId=patient-1");
    expect(calls[1].input).toBe("http://localhost:8080/api/delivery/results/ticket-1");
    expect(calls[2].input).toBe("http://localhost:8080/api/delivery/results/ticket-1/views");
    expect(calls[2].init?.method).toBe("POST");
    expect(calls[3].input).toBe(
      "http://localhost:8080/api/history/results?patientId=patient-1&testId=test-1",
    );
    expect(calls[4].input).toBe("http://localhost:8080/api/history/results?patientId=patient-1");
  });

  it("throws when backend responds with error", async () => {
    const fetcher: FetchLike = async () => new Response("Error", { status: 500 });
    const api = createResultsApi({ baseUrl: "http://localhost:8080", fetcher });

    await expect(api.listResults("patient-1")).rejects.toThrow(
      "Results API request failed with status 500.",
    );
  });

  it("passes token and session headers correctly", async () => {
    const calls: Array<{ input: string; init?: RequestInit }> = [];
    const fetcher: FetchLike = async (input, init) => {
      calls.push({ input, init });
      return new Response(JSON.stringify([]), { status: 200 });
    };
    const api = createResultsApi({
      baseUrl: "http://localhost",
      fetcher,
      getToken: () => "my-token",
      getSessionHeaders: () => ({ "X-Custom-Session": "session-1" }),
    });

    await api.listResults("patient-1");
    const headers = calls[0].init?.headers as Headers;
    expect(headers.get("Authorization")).toBe("Bearer my-token");
    expect(headers.get("X-Custom-Session")).toBe("session-1");
  });
});
