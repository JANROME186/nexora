import { afterEach, describe, expect, it, vi } from "vitest";
import {
  getAssistantSession,
  listAssistantAuditRecords,
  requestAssistantDraft,
  reviewAssistantDraft,
} from "../api/aiOverlayApi";

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

describe("aiOverlayApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    window.localStorage.clear();
  });

  it("requests an assistant draft through the generic assistant session endpoint", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { sessionId: "ai-1" } });
    await requestAssistantDraft({
      purpose: "result_case_summary",
      sourceContextType: "result",
      sourceContextId: "result-1",
      prompt: "Summarize",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/ai/assistant/sessions",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("loads a single assistant session via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { sessionId: "ai-1" } });
    await getAssistantSession("ai-1");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/ai/assistant/sessions/ai-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("reviews a draft via POST on the session review path", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { sessionId: "ai-1", reviewStatus: "accepted" } });
    await reviewAssistantDraft("ai-1", {
      reviewerId: "reviewer-1",
      decision: "accepted",
      reason: "Citations verified.",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/ai/assistant/sessions/ai-1/review",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("loads assistant audit records via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ sessionId: "ai-1" }] });
    await listAssistantAuditRecords();
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/ai/assistant/sessions/audit-records",
      expect.objectContaining({ method: "GET" }),
    );
  });
});
