import { describe, expect, it } from "vitest";
import { createResultDetailScreenModel } from "../screens/resultDetailScreenModel";
import { createResultsApi } from "../api/resultsApi";
import type { FetchLike } from "../api/platformFoundationApi";

describe("resultDetailScreenModel", () => {
  it("loads ticket detail, records view, and downloads report", async () => {
    const calls: string[] = [];
    const fetcher: FetchLike = async (input, init) => {
      calls.push(`${init?.method || "GET"} ${input}`);
      if (String(input).includes("/views")) {
        return new Response(null, { status: 204 });
      }
      return new Response(
        JSON.stringify({
          deliveryTicketId: "ticket-1",
          status: "delivered",
          reportUrl: "https://example.com/report.pdf",
        }),
        { status: 200 },
      );
    };
    const api = createResultsApi({ baseUrl: "https://test", fetcher });

    const model = createResultDetailScreenModel(api, "ticket-1");
    expect(model.getState().ticket).toBeNull();

    await model.loadDetail();

    expect(model.getState().ticket?.deliveryTicketId).toBe("ticket-1");
    expect(calls).toContain("GET https://test/api/delivery/results/ticket-1");
    expect(calls).toContain("POST https://test/api/delivery/results/ticket-1/views");

    const reportUrl = await model.downloadReport();
    expect(reportUrl).toBe("https://example.com/report.pdf");
  });

  it("handles loading error", async () => {
    const fetcher: FetchLike = async () => new Response("Error", { status: 500 });
    const api = createResultsApi({ baseUrl: "https://test", fetcher });

    const model = createResultDetailScreenModel(api, "ticket-1");
    await model.loadDetail();

    expect(model.getState().error).toContain("failed with status 500");
  });

  it("downloadReport returns null if reportUrl is missing", async () => {
    const fetcher: FetchLike = async () =>
      new Response(JSON.stringify({ deliveryTicketId: "ticket-1", reportUrl: null }), {
        status: 200,
      });
    const api = createResultsApi({ baseUrl: "https://test", fetcher });
    const model = createResultDetailScreenModel(api, "ticket-1");
    await model.loadDetail();
    const url = await model.downloadReport();
    expect(url).toBeNull();
  });
});
