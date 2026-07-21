import { useCallback, useEffect, useState } from "react";

const DEFAULT_COOLDOWN_SECONDS = 30;

/**
 * Tracks a client-side cooldown after a 429 response. The backend's `PublicApiRateLimitInterceptor`
 * does not send a `Retry-After` header (see PublicApiRateLimitInterceptor.java), so the frontend
 * cannot know the server's exact reset time; a fixed, conservative cooldown gives visitors a clear
 * "try again in N seconds" signal instead of letting them hammer the endpoint immediately again.
 */
export function useRateLimitCooldown(seconds = DEFAULT_COOLDOWN_SECONDS): {
  remainingSeconds: number;
  isActive: boolean;
  start: () => void;
} {
  const [remainingSeconds, setRemainingSeconds] = useState(0);

  useEffect(() => {
    if (remainingSeconds <= 0) {
      return;
    }
    const id = setTimeout(() => setRemainingSeconds((value) => Math.max(0, value - 1)), 1000);
    return () => clearTimeout(id);
  }, [remainingSeconds]);

  const start = useCallback(() => setRemainingSeconds(seconds), [seconds]);

  return { remainingSeconds, isActive: remainingSeconds > 0, start };
}
