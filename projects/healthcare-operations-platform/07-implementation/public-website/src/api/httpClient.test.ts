import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError, get, post } from "./httpClient";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("httpClient", () => {
  beforeEach(() => {
    mockFetch.mockReset();
  });

  it("performs a GET request and returns parsed JSON", async () => {
    mockFetch.mockResolvedValueOnce({ ok: true, status: 200, json: async () => ({ a: 1 }) });
    const result = await get("/api/public/catalog/tests/published?laboratoryId=lab-local");
    expect(result).toEqual({ a: 1 });
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/tests/published?laboratoryId=lab-local",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("performs a POST request with a JSON body", async () => {
    mockFetch.mockResolvedValueOnce({ ok: true, status: 201, json: async () => ({ id: "x" }) });
    const result = await post("/api/public/care-delivery/appointment-requests", { a: 1 });
    expect(result).toEqual({ id: "x" });
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/care-delivery/appointment-requests",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ a: 1 }),
        headers: expect.objectContaining({ "Content-Type": "application/json" }),
      }),
    );
  });

  it("returns undefined for a 204 response", async () => {
    mockFetch.mockResolvedValueOnce({ ok: true, status: 204 });
    const result = await get("/api/public/catalog/panels/published?laboratoryId=lab-local");
    expect(result).toBeUndefined();
  });

  it("throws an ApiError carrying the backend error code on a non-ok response", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 404,
      statusText: "Not Found",
      json: async () => ({
        status: 404,
        code: "PUBLIC_CATALOG_NOT_PUBLISHED",
        message: "not found",
      }),
    });

    await expect(get("/api/public/catalog/tests/x/published-snapshot")).rejects.toMatchObject({
      status: 404,
      code: "PUBLIC_CATALOG_NOT_PUBLISHED",
    });
  });

  it("falls back to statusText when the error body is not JSON", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 500,
      statusText: "Internal Server Error",
      json: async () => {
        throw new Error("not json");
      },
    });

    try {
      await get("/api/public/catalog/tests/published?laboratoryId=lab-local");
      expect.unreachable();
    } catch (error) {
      const apiError = error as ApiError;
      expect(apiError).toBeInstanceOf(ApiError);
      expect(apiError.message).toBe("Internal Server Error");
      expect(apiError.code).toBeUndefined();
    }
  });

  it("marks 429 responses as rate limited", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 429,
      statusText: "Too Many Requests",
      json: async () => ({ status: 429, code: "PUBLIC_RATE_LIMIT_EXCEEDED", message: "slow down" }),
    });

    try {
      await get("/api/public/catalog/tests/published?laboratoryId=lab-local");
      expect.unreachable();
    } catch (error) {
      const apiError = error as ApiError;
      expect(apiError.isRateLimited).toBe(true);
    }
  });

  it("wraps a network failure as a NETWORK_ERROR ApiError", async () => {
    mockFetch.mockRejectedValueOnce(new TypeError("Failed to fetch"));

    try {
      await get("/api/public/catalog/tests/published?laboratoryId=lab-local");
      expect.unreachable();
    } catch (error) {
      const apiError = error as ApiError;
      expect(apiError.status).toBe(0);
      expect(apiError.code).toBe("NETWORK_ERROR");
      expect(apiError.isRateLimited).toBe(false);
    }
  });
});
