import { act, renderHook, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { useAsyncAction } from "./useAsyncAction";

describe("useAsyncAction", () => {
  it("starts idle and transitions to success", async () => {
    const action = vi.fn().mockResolvedValue({ ok: true });
    const { result } = renderHook(() => useAsyncAction(action));

    expect(result.current.status).toBe("idle");

    let outcome;
    await act(async () => {
      outcome = await result.current.run("arg-1");
    });

    expect(outcome).toEqual({ ok: true, data: { ok: true } });
    expect(result.current.status).toBe("success");
    expect(result.current.data).toEqual({ ok: true });
    expect(action).toHaveBeenCalledWith("arg-1");
  });

  it("transitions to error and exposes the thrown value", async () => {
    const failure = new Error("boom");
    const action = vi.fn().mockRejectedValue(failure);
    const { result } = renderHook(() => useAsyncAction(action));

    let outcome;
    await act(async () => {
      outcome = await result.current.run();
    });

    expect(outcome).toEqual({ ok: false, error: failure });
    expect(result.current.status).toBe("error");
    expect(result.current.error).toBe(failure);
  });

  it("resets back to idle", async () => {
    const action = vi.fn().mockResolvedValue("done");
    const { result } = renderHook(() => useAsyncAction(action));

    await act(async () => {
      await result.current.run();
    });
    expect(result.current.status).toBe("success");

    act(() => result.current.reset());
    await waitFor(() => expect(result.current.status).toBe("idle"));
  });
});
