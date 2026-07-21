import { act, renderHook } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { useRateLimitCooldown } from "./useRateLimitCooldown";

describe("useRateLimitCooldown", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("is inactive until started", () => {
    const { result } = renderHook(() => useRateLimitCooldown(5));
    expect(result.current.isActive).toBe(false);
    expect(result.current.remainingSeconds).toBe(0);
  });

  it("counts down to zero after starting", () => {
    const { result } = renderHook(() => useRateLimitCooldown(2));

    act(() => result.current.start());
    expect(result.current.isActive).toBe(true);
    expect(result.current.remainingSeconds).toBe(2);

    act(() => {
      vi.advanceTimersByTime(1000);
    });
    expect(result.current.remainingSeconds).toBe(1);

    act(() => {
      vi.advanceTimersByTime(1000);
    });
    expect(result.current.remainingSeconds).toBe(0);
    expect(result.current.isActive).toBe(false);
  });
});
