import { describe, expect, it } from "vitest";
import { createResultsScreenModel } from "../screens/resultsScreenModel";
import { createResultsApi } from "../api/resultsApi";
import type { FetchLike } from "../api/platformFoundationApi";

describe("resultsScreenModel", () => {
  it("loads tickets successfully", async () => {
    const fetcher: FetchLike = async () =>
      new Response(JSON.stringify([{ deliveryTicketId: "ticket-1" }]), { status: 200 });
    const api = createResultsApi({ baseUrl: "https://test", fetcher });

    let notifications = 0;
    const model = createResultsScreenModel(
      api,
      {
        userId: "patient-1",
        tenantId: "tenant-1",
        displayName: "Test",
        email: "test@example.com",
        createdAt: "2026",
        token: "",
      },
      () => {
        notifications++;
      },
    );

    expect(model.getState().isLoading).toBe(false);
    expect(model.getState().tickets).toEqual([]);

    await model.loadTickets();

    expect(notifications).toBe(2);
    expect(model.getState().isLoading).toBe(false);
    expect(model.getState().tickets).toHaveLength(1);
    expect(model.getState().error).toBeNull();
  });

  it("handles loading error", async () => {
    const fetcher: FetchLike = async () => new Response("Error", { status: 500 });
    const api = createResultsApi({ baseUrl: "https://test", fetcher });

    const model = createResultsScreenModel(api, {
      userId: "patient-1",
      tenantId: "tenant-1",
      displayName: "Test",
      email: "test@example.com",
      createdAt: "2026",
      token: "",
    });

    await model.loadTickets();
    expect(model.getState().error).toContain("Results API request failed");
  });
});
