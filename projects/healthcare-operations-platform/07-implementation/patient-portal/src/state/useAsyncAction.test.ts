import { act, renderHook } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import { useAsyncAction } from "./useAsyncAction";
import { ApiError } from "../api/httpClient";

describe("useAsyncAction", () => {
  it("manages success states", async () => {
    const mockAction = vi.fn().mockResolvedValue("success-data");
    const { result } = renderHook(() => useAsyncAction(mockAction));

    expect(result.current.status).toBe("idle");

    let runResult;
    await act(async () => {
      runResult = await result.current.run();
    });

    expect(result.current.status).toBe("success");
    expect(result.current.data).toBe("success-data");
    expect(runResult).toEqual({ ok: true, data: "success-data" });

    // test reset
    act(() => {
      result.current.reset();
    });
    expect(result.current.status).toBe("idle");
    expect(result.current.data).toBeUndefined();
  });

  it("manages error states with ApiError", async () => {
    const mockAction = vi.fn().mockRejectedValue(new ApiError(400, "Bad Request"));
    const { result } = renderHook(() => useAsyncAction(mockAction));

    let runResult;
    await act(async () => {
      runResult = await result.current.run();
    });

    expect(result.current.status).toBe("error");
    expect(result.current.errorMessage).toBe("Bad Request");
    expect(runResult).toEqual({ ok: false, errorMessage: "Bad Request" });
  });

  it("manages generic error states", async () => {
    const mockAction = vi.fn().mockRejectedValue(new Error("Generic error"));
    const { result } = renderHook(() => useAsyncAction(mockAction));

    let runResult;
    await act(async () => {
      runResult = await result.current.run();
    });

    expect(result.current.status).toBe("error");
    expect(runResult).toEqual({ ok: false, errorMessage: "Error inesperado. Inténtalo de nuevo." });
  });
});
