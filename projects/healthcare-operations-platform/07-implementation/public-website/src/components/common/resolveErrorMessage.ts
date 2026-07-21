import { ApiError } from "../../api/httpClient";
import type { MessageCatalog } from "../../i18n/locales/es-MX";

type ErrorMessages = MessageCatalog["errors"];

const KNOWN_CODES = new Set<keyof ErrorMessages>([
  "PUBLIC_RATE_LIMIT_EXCEEDED",
  "PUBLIC_CATALOG_NOT_PUBLISHED",
  "PUBLIC_APPOINTMENT_REQUEST_INVALID",
  "PUBLIC_QUOTATION_REQUEST_INVALID",
  "PUBLIC_PROSPECTIVE_CONTACT_REQUIRED",
]);

function isKnownCode(code: string | undefined): code is keyof ErrorMessages {
  return code !== undefined && KNOWN_CODES.has(code as keyof ErrorMessages);
}

/**
 * Resolves an unknown error into a localized, user-safe message. Never surfaces the raw server
 * `message` text (which is English-only and not meant for display) except as an implicit last
 * resort inside the generic bucket, so the UI stays fully externalized/localizable.
 */
export function resolveErrorMessage(error: unknown, errors: ErrorMessages): string {
  if (error instanceof ApiError) {
    if (error.code === "NETWORK_ERROR" || error.status === 0) {
      return errors.network;
    }
    if (isKnownCode(error.code)) {
      return errors[error.code];
    }
    if (error.isRateLimited) {
      return errors.PUBLIC_RATE_LIMIT_EXCEEDED;
    }
  }
  return errors.generic;
}
