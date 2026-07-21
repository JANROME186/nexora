import { useCallback, useState } from "react";

export type AsyncStatus = "idle" | "loading" | "success" | "error";

export interface AsyncActionState<TResult> {
  status: AsyncStatus;
  data?: TResult;
  error?: unknown;
}

export type AsyncActionResult<TResult> =
  { ok: true; data: TResult } | { ok: false; error: unknown };

export interface UseAsyncActionResult<
  TArgs extends unknown[],
  TResult,
> extends AsyncActionState<TResult> {
  run: (...args: TArgs) => Promise<AsyncActionResult<TResult>>;
  reset: () => void;
}

/**
 * Tracks loading/success/error state for a single async action (a public form submission).
 * `error` is kept as the raw thrown value (an `ApiError` for HTTP failures) rather than a
 * pre-formatted string, so callers can resolve a localized message via `resolveErrorMessage` and
 * branch on `ApiError.isRateLimited` to drive 429-specific UI.
 */
export function useAsyncAction<TArgs extends unknown[], TResult>(
  action: (...args: TArgs) => Promise<TResult>,
): UseAsyncActionResult<TArgs, TResult> {
  const [state, setState] = useState<AsyncActionState<TResult>>({ status: "idle" });

  const run = useCallback(
    async (...args: TArgs): Promise<AsyncActionResult<TResult>> => {
      setState({ status: "loading" });
      try {
        const data = await action(...args);
        setState({ status: "success", data });
        return { ok: true, data };
      } catch (error) {
        setState({ status: "error", error });
        return { ok: false, error };
      }
    },
    [action],
  );

  const reset = useCallback(() => setState({ status: "idle" }), []);

  return { ...state, run, reset };
}
