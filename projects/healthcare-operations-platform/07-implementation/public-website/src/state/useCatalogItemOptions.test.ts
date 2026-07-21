import { renderHook, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useCatalogItemOptions } from "./useCatalogItemOptions";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("useCatalogItemOptions", () => {
  beforeEach(() => {
    mockFetch.mockReset();
  });

  it("combines published tests and panels into one option list", async () => {
    mockFetch.mockImplementation((url: string) => {
      if (url.includes("/tests/")) {
        return Promise.resolve({
          ok: true,
          status: 200,
          json: async () => [
            { testDefinitionId: "t1", code: "GLU", nameEn: "Glucose", nameEs: "Glucosa" },
          ],
        });
      }
      return Promise.resolve({
        ok: true,
        status: 200,
        json: async () => [{ panelId: "p1", code: "CBC", nameEn: "Panel", nameEs: "Panel" }],
      });
    });

    const { result } = renderHook(() => useCatalogItemOptions("es-MX"));

    expect(result.current.status).toBe("loading");
    await waitFor(() => expect(result.current.status).toBe("success"));
    expect(result.current.options).toEqual([
      { id: "t1", kind: "test", label: "Glucosa" },
      { id: "p1", kind: "panel", label: "Panel" },
    ]);
  });

  it("sets status to error when either request fails", async () => {
    mockFetch.mockRejectedValue(new Error("network down"));
    const { result } = renderHook(() => useCatalogItemOptions("en-US"));
    await waitFor(() => expect(result.current.status).toBe("error"));
    expect(result.current.options).toEqual([]);
  });
});
