import { afterEach, describe, expect, it, vi } from "vitest";
import {
  listPublishedDiagnosticServices,
  listPublishedPanels,
  listPublishedPreparations,
  listPublishedTests,
} from "../api/publicContentApi";

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

describe("publicContentApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    window.localStorage.clear();
  });

  it("lists published diagnostic services for a laboratory", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: [
        {
          serviceId: "service-1",
          code: "SVC-1",
          nameEn: "Panel",
          nameEs: "Panel",
          serviceType: "profile",
          version: 2,
        },
      ],
    });

    const services = await listPublishedDiagnosticServices("lab-1");

    expect(services).toHaveLength(1);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/public/catalog/diagnostic-services/published?laboratoryId=lab-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("lists published tests for a laboratory", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [] });

    await listPublishedTests("lab-1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/public/catalog/tests/published?laboratoryId=lab-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("lists published panels for a laboratory", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [] });

    await listPublishedPanels("lab-1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/public/catalog/panels/published?laboratoryId=lab-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("lists published preparations for a laboratory", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [] });

    await listPublishedPreparations("lab-1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/public/catalog/preparations/published?laboratoryId=lab-1",
      expect.objectContaining({ method: "GET" }),
    );
  });
});
