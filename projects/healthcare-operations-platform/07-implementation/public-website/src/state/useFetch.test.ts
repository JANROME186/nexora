import { act, renderHook, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { useFetch } from "./useFetch";

describe("useFetch", () => {
  it("starts loading and resolves to success", async () => {
    const load = vi.fn().mockResolvedValue(["a", "b"]);
    const { result } = renderHook(() => useFetch(load, "lab-local"));

    expect(result.current.status).toBe("loading");
    await waitFor(() => expect(result.current.status).toBe("success"));
    expect(result.current.data).toEqual(["a", "b"]);
  });

  it("resolves to error when load rejects", async () => {
    const load = vi.fn().mockRejectedValue(new Error("boom"));
    const { result } = renderHook(() => useFetch(load, "lab-local"));

    await waitFor(() => expect(result.current.status).toBe("error"));
    expect(result.current.error).toBeInstanceOf(Error);
  });

  it("re-fetches when the dependency changes", async () => {
    const load = vi.fn().mockResolvedValue("first");
    const { result, rerender } = renderHook(({ dep }) => useFetch(load, dep), {
      initialProps: { dep: "a" },
    });

    await waitFor(() => expect(result.current.status).toBe("success"));
    expect(load).toHaveBeenCalledTimes(1);

    load.mockResolvedValue("second");
    rerender({ dep: "b" });
    await waitFor(() => expect(load).toHaveBeenCalledTimes(2));
    await waitFor(() => expect(result.current.data).toBe("second"));
  });

  it("retry triggers another fetch attempt", async () => {
    const load = vi.fn().mockResolvedValue("value");
    const { result } = renderHook(() => useFetch(load, "lab-local"));

    await waitFor(() => expect(result.current.status).toBe("success"));
    expect(load).toHaveBeenCalledTimes(1);

    act(() => result.current.retry());
    await waitFor(() => expect(load).toHaveBeenCalledTimes(2));
  });
});
