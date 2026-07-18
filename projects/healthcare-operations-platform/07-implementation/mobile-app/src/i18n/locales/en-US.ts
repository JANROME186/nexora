import type { MessageCatalog } from "./es-MX";

/**
 * en-US message catalog (fallback locale per the enterprise-product-foundation-standard
 * `localization_and_i18n` foundation: `fallback_locale: en-US`).
 *
 * Keeps the exact original English text that used to be the sole content of
 * `src/i18n/messages.ts`. Typed against `MessageCatalog` (derived from `es-MX.ts`) so TypeScript
 * enforces key parity between the two locales.
 */
export const enUS: MessageCatalog = {
  tenantIdRequired: "Tenant id is required.",
  userIdRequired: "User id is required.",
  displayNameRequired: "Display name is required.",
  emailRequired: "Email is required.",
  emailInvalid: "Email must be valid.",
  sessionRequired: "Authenticated session is required.",
};
