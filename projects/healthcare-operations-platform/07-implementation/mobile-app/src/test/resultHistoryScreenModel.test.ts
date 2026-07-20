import { describe, expect, it } from "vitest";
import { createResultHistoryScreenModel } from "../screens/resultHistoryScreenModel";
import { createResultsApi } from "../api/resultsApi";
import type { ResultsApi } from "../api/resultsApi";
import type { FetchLike } from "../api/platformFoundationApi";

describe("resultHistoryScreenModel", () => {
  it("loads history successfully", async () => {
    const fetcher: FetchLike = async () =>
      new Response(JSON.stringify([{ testId: "test-1", entries: [] }]), { status: 200 });
    const api = createResultsApi({ baseUrl: "https://test", fetcher });

    const model = createResultHistoryScreenModel(api, {
      userId: "patient-1",
      tenantId: "tenant-1",
      displayName: "Test",
      email: "test@example.com",
      createdAt: "2026",
      token: "",
      roleCodes: ["PATIENT"],
    });

    await model.loadHistory();
    expect(model.getState().history).toHaveLength(1);
    expect(model.getState().error).toBeNull();
  });

  it("handles loading error", async () => {
    const fetcher: FetchLike = async () => new Response("Error", { status: 500 });
    const api = createResultsApi({ baseUrl: "https://test", fetcher });

    const model = createResultHistoryScreenModel(api, {
      userId: "patient-1",
      tenantId: "tenant-1",
      displayName: "Test",
      email: "test@example.com",
      createdAt: "2026",
      token: "",
      roleCodes: ["PATIENT"],
    });

    await model.loadHistory();
    expect(model.getState().error).toContain("failed with status 500");
  });

  it("handles non-Error exceptions and state change callback", async () => {
    let notifications = 0;
    const api = {
      getResultHistory: () => Promise.reject("String Error"),
    } as unknown as ResultsApi;

    const model = createResultHistoryScreenModel(
      api,
      {
        userId: "patient-1",
        tenantId: "tenant-1",
        displayName: "Test",
        email: "test@example.com",
        createdAt: "2026",
        token: "",
        roleCodes: ["PATIENT"],
      },
      () => {
        notifications++;
      },
    );

    await model.loadHistory();
    expect(model.getState().error).toBe("String Error");
    expect(notifications).toBeGreaterThan(0);
  });
});
