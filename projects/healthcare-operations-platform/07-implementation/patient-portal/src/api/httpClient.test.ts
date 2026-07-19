import { describe, it, expect, vi, beforeEach } from "vitest";
import { get, post, put, ApiError } from "./httpClient";
import { getPatientHistory } from "./patientResultHistoryApi";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("HTTP Client & API layer", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockFetch.mockReset();
    localStorage.clear();
  });

  it("handles successful GET request", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ data: "ok" }),
    });

    const res = await get("/api/test");
    expect(res).toEqual({ data: "ok" });
    expect(mockFetch).toHaveBeenCalledWith("/api/test", expect.objectContaining({
      method: "GET",
    }));
  });

  it("handles successful POST request", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ created: true }),
    });

    const res = await post("/api/test", { name: "test-item" });
    expect(res).toEqual({ created: true });
    expect(mockFetch).toHaveBeenCalledWith("/api/test", expect.objectContaining({
      method: "POST",
      body: JSON.stringify({ name: "test-item" }),
    }));
  });

  it("handles successful PUT request", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ updated: true }),
    });

    const res = await put("/api/test", { id: 1 });
    expect(res).toEqual({ updated: true });
    expect(mockFetch).toHaveBeenCalledWith("/api/test", expect.objectContaining({
      method: "PUT",
      body: JSON.stringify({ id: 1 }),
    }));
  });

  it("handles 204 No Content response", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 204,
    });

    const res = await get("/api/empty");
    expect(res).toBeUndefined();
  });

  it("throws ApiError with message on non-ok responses", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 400,
      statusText: "Bad Request",
      json: async () => ({ message: "Specific API Error Message" }),
    });

    try {
      await get("/api/fail");
      expect.fail("Should have thrown an error");
    } catch (e: any) {
      expect(e).toBeInstanceOf(ApiError);
      expect(e.status).toBe(400);
      expect(e.message).toBe("Specific API Error Message");
    }
  });

  it("throws ApiError with statusText when JSON parsing fails or message is missing", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 500,
      statusText: "Internal Server Error",
      json: async () => { throw new Error("not json"); },
    });

    try {
      await get("/api/fail");
      expect.fail("Should have thrown an error");
    } catch (e: any) {
      expect(e).toBeInstanceOf(ApiError);
      expect(e.status).toBe(500);
      expect(e.message).toBe("Internal Server Error");
    }
  });

  it("executes getPatientHistory successfully", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ patientId: "Patient-01", entries: [] }),
    });

    const res = await getPatientHistory("Patient-01");
    expect(res).toEqual({ patientId: "Patient-01", entries: [] });
    expect(mockFetch).toHaveBeenCalledWith("/api/results/history/patient/Patient-01", expect.any(Object));
  });
});
