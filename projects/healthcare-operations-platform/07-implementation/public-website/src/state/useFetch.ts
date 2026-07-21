import { useCallback, useEffect, useState } from "react";

export type FetchStatus = "loading" | "success" | "error";

export interface FetchState<TResult> {
  status: FetchStatus;
  data?: TResult;
  error?: unknown;
}

/**
 * Runs `load` on mount and whenever `dep` changes, exposing loading/success/error state plus a
 * manual `retry`. Used by every catalog list/detail page so loading, empty and error states are
 * handled the same way across the site. `dep` is a single string (a laboratory id or catalog item
 * id) rather than an arbitrary array so the effect dependency list stays a literal the
 * react-hooks/set-state-in-effect and useCallback-array-literal lint rules can statically verify.
 */
export function useFetch<TResult>(
  load: () => Promise<TResult>,
  dep: string,
): FetchState<TResult> & { retry: () => void } {
  const [state, setState] = useState<FetchState<TResult>>({ status: "loading" });
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    let cancelled = false;
    // Resetting to "loading" when `dep`/`attempt` change is the intended behavior: it replaces
    // stale data from a previous id/attempt while the new fetch is in flight.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setState({ status: "loading" });
    load()
      .then((data) => {
        if (!cancelled) {
          setState({ status: "success", data });
        }
      })
      .catch((error: unknown) => {
        if (!cancelled) {
          setState({ status: "error", error });
        }
      });
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dep, attempt]);

  const retry = useCallback(() => setAttempt((value) => value + 1), []);

  return { ...state, retry };
}
