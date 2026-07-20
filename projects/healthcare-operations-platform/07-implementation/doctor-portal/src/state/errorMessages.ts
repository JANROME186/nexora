import { ApiError } from "../api/httpClient";
import type { MessageCatalog } from "../i18n/locales/es-MX";

/**
 * Resolves an unknown error thrown by a doctor-portal API call into the correct user-facing
 * state message, distinguishing session-expiry (401) from missing-permission (403) from a
 * generic failure, per the explicit loading/empty/error/no-permission/session-expired state
 * requirement for COM-MOD-009-PORTAL-002. Centralized here so every tab reports these states the
 * same way instead of duplicating status-code branching.
 *
 * @param onSessionExpired invoked as a side effect when the error is a 401, so the caller can log
 * the doctor out and force a fresh login.
 */
export function resolveApiErrorMessage(
  error: unknown,
  t: MessageCatalog,
  onSessionExpired: () => void,
): string {
  if (error instanceof ApiError) {
    if (error.status === 401) {
      onSessionExpired();
      return t.appShell.states.sessionExpired;
    }
    if (error.status === 403) {
      return t.appShell.states.noPermission;
    }
  }
  return t.appShell.states.error;
}
