import { useCallback, useState } from "react";
import { ApiError } from "../api/httpClient";
import { MESSAGES } from "../i18n/messages";

export type AsyncStatus = "idle" | "loading" | "success" | "error";

export interface AsyncActionState<TResult> {
  status: AsyncStatus;
  data?: TResult;
  errorMessage?: string;
}

export type AsyncActionResult<TResult> =
  { ok: true; data: TResult } | { ok: false; errorMessage: string };

export interface UseAsyncActionResult<
  TArgs extends unknown[],
  TResult,
> extends AsyncActionState<TResult> {
  run: (...args: TArgs) => Promise<AsyncActionResult<TResult>>;
  reset: () => void;
}

/**
 * Tracks loading, success and error state for a single async administration action
 * (create, query or assign), keeping screens free of ad-hoc state juggling.
 *
 * `run` resolves with an explicit `{ ok, data | errorMessage }` result so callers can
 * branch on the outcome without relying on stale hook state from a prior render, which
 * matters for actions such as role assignment that resolve with no response body.
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
        const message = error instanceof ApiError ? error.message : MESSAGES.unexpectedError;
        setState({ status: "error", errorMessage: message });
        return { ok: false, errorMessage: message };
      }
    },
    [action],
  );

  const reset = useCallback(() => setState({ status: "idle" }), []);

  return { ...state, run, reset };
}
